package org.ragna.comet
package validation

import data.DataFormat
import exception.stream.timed.StreamTimeoutException
import exception.stream.validations.*
import stream.StreamSource
import stream.extractors.StreamExtractor
import trigger.{TriggerShapeMap, TriggerTargetDeclarations, ValidationTrigger}
import validation.Validator.Messages.*
import validation.Validator.Types.{RDFValidationItem, RDFValidationReport}
import validation.configuration.ValidatorConfiguration
import validation.result.ResultStatus.*
import validation.result.ValidationResult

import cats.data.EitherT
import cats.effect.unsafe.implicits.global
import cats.effect.{Deferred, IO, Resource}
import cats.implicits.*
import cats.syntax.all.*
import es.weso.rdf.InferenceEngine
import es.weso.rdf.jena.RDFAsJenaModel
import es.weso.schema.{Schema, ShapeMapTrigger, Result as ValidationReport, ValidationTrigger as ValidationTriggerShaclex}
import fs2.{Pipe, Stream}
import org.apache.kafka.common.KafkaException

/**
 * Validator object capable of extracting data from a Stream and produce a
 * stream of [[ValidationResult]]s
 *
 * The whole validation pipeline is done through FS2 streams, and flows as follows:
 *
 * 1. A => RDF data: a Stream of data is received as instances of [[A]] that are
 * parsed for RDF data (this functionality is delegated to [[StreamExtractor]]s)
 *
 * 2. RDF data => Validation Report => The parsed data is validated and
 * the [[ValidationReport]]s included on a new Stream
 *
 * 3. Validation Report => Validation Result: The validated data is formatted 
 * and wrapped into instances of [[ValidationResult]]
 *
 * @param configuration Configuration of this validator instance
 * @param extractor     Extractor providing access to the incoming RDF items
 * @tparam A Type of the items being received via Stream and thus feeding
 *           the validator
 * @throws StreamValidationException When invalid or erroring items are found
 *                                   and the stream is configured to stop in
 *                                   such cases
 *
 */
class Validator[A](configuration: ValidatorConfiguration,
                   private val extractor: StreamExtractor[A]) {

  /**
   * Schema against which this validator's data is validated
   */
  val schema: Schema = configuration.schema

  /**
   * Validation trigger with which this validator's data is validated
   *
   * For ShapeMaps, it will be fully filled with the data and schema prefix maps 
   * before validation (see [[validateItems]])
   */
  val validationTrigger: ValidationTrigger = configuration.trigger

  // Shorthands for useful information

  /**
   * Source from which this validator's data arrives
   */
  val dataSource: StreamSource = extractor.source

  /**
   * Data format expected by this this validator
   */
  val dataFormat: DataFormat = extractor.format

  // Expose useful extractor information

  /**
   * Data inference applied by this this validator
   */
  val dataInference: InferenceEngine = extractor.inference

  /**
   * Alternative constructor, automatically build the validator configuration
   * given a schema and a trigger
   */
  def this(extractor: StreamExtractor[A], schema: Schema, trigger: ValidationTrigger) = {
    this(ValidatorConfiguration(schema, trigger), extractor)
  }

  /**
   * Main method of the validator, produces a Stream of validation results
   * from input data, fetching and processing items as specified in
   * this validator's configurations
   *
   * Error handling: see "throws" section. Also, it is highly encouraged to 
   * handle any other possible exception that might happen
   *
   * @return A [[Stream]] of [[ValidationResult]]s to be further processed by
   *         third parties
   *
   * @throws StreamValidationException when errored/invalid results are emitted but are not allowed
   *                                   in the [[configuration]]
   * @throws StreamTimeoutException    If the time between received items exceeds
   *                                   the one configured in the extractor
   * @throws KafkaException            When using a Kafka extractor and any
   *                                   kafka-related failure occurs (i.e.:
   *                                   underlying Kafka consumer cannot be built,
   *                                   connection fails, server goes offline...)
   */
  def validate: Stream[IO, ValidationResult] =
    extractor.dataStream // Initial data stream
      .through(validateItems) // Transform to validations
      .through(createResults) // Transform to results
  //      .onFinalize(IO.println(streamFinalized)) // Logger info message when done

  /**
   * Stream transformation pipe in charge of validating RDF items and
   * emitting the resulting validation reports
   *
   * @return A new transformed stream: RDF data => Validation Report
   */
  private def validateItems: Pipe[IO, RDFValidationItem, RDFValidationReport] =
    (inputItems: Stream[IO, RDFValidationItem]) => {
      // EvalMap: must output IO[RDFValidation],
      // that is: IO[Either[Throwable, ValidationReport]]
      inputItems.parEvalMap(configuration.concurrentItems) {
        // Propagate error
        case Left(err) => IO.pure(Left(err))
        // Validate data
        case Right(rdfResource) =>
          val validationResult: IO[ValidationReport] = for {
            // Get a builder
            builderResource <- RDFAsJenaModel.empty
            validation: ValidationReport <- (builderResource, rdfResource)
              .tupled
              .use {
                case (builder, rdfModel) =>
                  /* From our adapter ValidationTrigger, get the final trigger
                  used by Shaclex, as follows:
                  - If trigger is ShapeMap, complete it with the now known data/schema
                    prefix maps (for target declarations: do nothing)
                  - Get the Shaclex trigger from our trigger instance. Return an
                    errored result if the trigger turned out invalid
                  */
                  val eitherFinalValidationTrigger = (
                    validationTrigger match {
                      case tsm: TriggerShapeMap =>
                        // Forced to unsafe run because of ShaclEx implementation
                        tsm.copy(nodesPrefixMap = rdfModel.getPrefixMap.unsafeRunSync(), shapesPrefixMap = schema.pm)
                      case _: TriggerTargetDeclarations => validationTrigger
                    }).getValidationTrigger

                  eitherFinalValidationTrigger match {
                    // Could not create validation trigger, error result
                    case Left(errorList) =>
                      IO.pure(ValidationReport.errStr(
                        s"""$invalidValidationTrigger: 
                           |${errorList.mkString(", ")}
                           |""".stripMargin.strip()))
                    // Validation trigger is OK, attempt validation
                    case Right(trigger) =>
                      schema.validate(rdfModel, trigger, builder)
                  }
              }
          } yield validation
          // Map any exception to Either
          validationResult.attempt
      }
    }

  /**
   * Stream transformation pipe in charge of transforming RDF validation reports
   * to the final instances of [[ValidationResult]] emitted by the validator
   *
   * @return A new transformed stream: Validation Report => Validation Result
   */
  private def createResults: Pipe[IO, RDFValidationReport, ValidationResult] =
    (inputValidations: Stream[IO, RDFValidationReport]) => {
      inputValidations.parEvalMap(configuration.concurrentItems) { validation =>
        IO {
          // Make final result
          val validationResult = ValidationResult(validation)
          // Raise errors to halt the stream, depending on the configuration
          if (configuration.haltOnErrored && validationResult.status == ERRORED)
            throw StreamErroredItemException(
              cause = validation.left.toOption
            )
          else if (configuration.haltOnInvalid && validationResult.status == INVALID)
            throw StreamInvalidItemException(
              cause = validation.left.toOption,
              // Keep optionals, although the result will certainly be filled
              reason = validationResult.result
            )

          // Return the final validation result
          validationResult
        }
      }
    }
}

/**
 * Helper utilities for all Validators
 */
private[comet] object Validator {

  /**
   * Auxiliary messages emitted by the validator
   */
  object Messages {
    /**
     * Message logged whenever the validator Stream finalizes
     */
    val streamFinalized: String = "Validation stream finalized"

    /**
     * Message thrown when the input validation trigger can't be
     * processed or created given the data/schema used in the validation
     */
    val invalidValidationTrigger: String =
      "Could not process the provided validation trigger"
  }

  /**
   * Helper types to reduce code verbosity
   *
   * @note For types related to the validation pipeline, we drag errors
   *       inside instances of Either so that they don't reach the Stream and
   *       stop it
   *
   *       This way, when an error occurs, we can still emit [[ValidationResult]]s
   *
   */
  object Types {
    /**
     * Type representing a Cats Effect resource containing RDF Data ready to be
     * used and then discarded
     *
     * Left shall contain any error thrown during the RDF parsing process
     */
    type RDFValidationItem = Either[Throwable, Resource[IO, RDFAsJenaModel]]

    /**
     * Type representing the results of a schema validation over some RDF data
     *
     * Left shall contain any error thrown during the RDF validation process
     */
    type RDFValidationReport = Either[Throwable, ValidationReport]
  }

}
