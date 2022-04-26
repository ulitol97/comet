package org.ragna.comet
package extraction

import data.DataFormat
import data.DataFormat.*
import exception.stream.timed.StreamTimeoutException
import implicits.RDFElementImplicits.rdfFromString
import schema.ShExSchemaFormat
import schema.ShExSchemaFormat.*
import stream.extractors.StreamExtractor
import stream.extractors.file.FileExtractor
import stream.extractors.kafka.{KafkaExtractor, KafkaExtractorConfiguration}
import stream.extractors.list.ListExtractor
import trigger.ShapeMapFormat.*
import trigger.TriggerModeType.{SHAPEMAP, TARGET_DECLARATIONS}
import trigger.{ShapeMapFormat, TriggerModeType, ValidationTrigger}
import utils.Samples.StreamSamples.mkSingleValidationResult
import utils.{FileUtils, Samples}
import validation.Validator
import validation.configuration.ValidatorConfiguration
import validation.result.ResultStatus.*
import validation.result.ValidationResult

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import es.weso.schema.Schema
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration.*
import scala.language.postfixOps

/**
 * Test suite checking that the available basic data extractors work
 *
 * The testing goes as follows:
 *
 * - Some trivial valid RDF/Schema combinations will be produced, 
 * complying with the apps testing framework (see [[Samples]])
 *
 * - This same data will be consumed by a validator, but each time the validator
 * will be fed through a different extractor to make sure all extractors work
 * equally and are correctly abstracted
 *
 * - It will be tested that as many results are produced as inputs given to the
 * extractor, as well as that results are VALID and not ERRORED
 *
 * Tests are nested as follows to cover all possibilities:
 *
 * - Per extractor type (List extractor, File extractor, Kafka extractor)
 *
 * @note In these tests we take for granted the functioning of the validator
 *       and we are just interested in the extractors
 * @note For obvious reasons, the Kafka Extractor can't be tested in a unit test
 *       (expect integration tests) and the Kafka extractor is therefore
 *       expected to timeout and fail
 */
//noinspection RedundantDefaultArgument
class ExtractorTests extends AsyncFreeSpec with AsyncIOSpec with Matchers {

  /**
   * Number of RDF elements to generate and feed to each extractor
   */
  private val numberOfItems = 5

  "LIST extractor" - {
    // Trivial example with a list extractor, the validation goes OK
    "works" in {
      val validationResults: IO[List[ValidationResult]] =
        for {
          // Data, schema, trigger
          schema <- Samples.SchemaSamples.mkSchemaShExIO()
          trigger = Samples.TriggerSamples.mkTriggerShex()
          rdfItems = Samples.RdfSamples.mkRdfItems(numberOfItems, TURTLE)

          // List extractor
          extractor = ListExtractor(rdfItems, DataFormat.TURTLE)

          // Validator init
          configuration = ValidatorConfiguration(schema, trigger)
          validator = new Validator(configuration, extractor)

          results: List[ValidationResult] <- validator.validate.compile.toList
        } yield results

      // Same amount of outputs, all VALID
      validationResults.asserting(results => {
        results.length shouldBe numberOfItems
        results.forall(_.status == VALID) shouldBe true
      })
    }
  }

  "FILE extractor" - {
    // Trivial example with a file extractor, the validation goes OK
    "works" in {
      // Create a resource with temporary files containing RDF
      // The files will be read and eventually deleted
      val rdfItems = Samples.RdfSamples.mkRdfItems(numberOfItems, TURTLE)
      val filesResource = FileUtils.createFiles(rdfItems)

      val validationResults: IO[List[ValidationResult]] = filesResource.use(
        filePaths =>
          for {
            // Schema, trigger
            schema <- Samples.SchemaSamples.mkSchemaShExIO()
            trigger = Samples.TriggerSamples.mkTriggerShex()

            // File extractor
            extractor = FileExtractor(filePaths, format = TURTLE)

            // Validator init
            configuration = ValidatorConfiguration(schema, trigger)
            validator = new Validator(configuration, extractor)

            results: List[ValidationResult] <- validator.validate.compile.toList
          } yield results
      )

      // Same amount of outputs, all VALID
      validationResults.asserting(results => {
        results.length shouldBe numberOfItems
        results.forall(_.status == VALID) shouldBe true
      })
    }
  }

  "KAFKA extractor" - {
    // Trivial example with a Kafka extractor
    // Expected to timeout in the absence of a local Stream
    "works" in {
      val validationResult: IO[Either[Throwable, ValidationResult]] =
        for {
          // Data, schema, trigger
          schema <- Samples.SchemaSamples.mkSchemaShExIO()
          trigger = Samples.TriggerSamples.mkTriggerShex()

          // Kafka extractor
          extractorConfiguration = KafkaExtractorConfiguration("topic")
          extractor = KafkaExtractor[Unit, String](
            extractorConfiguration,
            TURTLE,
            itemTimeout = Some(1 second)
          )

          // Validator init
          validatorConfiguration = ValidatorConfiguration(schema, trigger)
          validator = new Validator(validatorConfiguration, extractor)

          results: List[Either[Throwable, ValidationResult]] <- validator.validate
            .attempt
            .compile
            .toList
        } yield results.head

      // Result is an error and of type StreamTimeoutException
      validationResult.asserting(results => {
        results.isLeft shouldBe true

        results.swap.toOption.get.isInstanceOf[StreamTimeoutException]
          .shouldBe(true)
      })
    }
  }
}
