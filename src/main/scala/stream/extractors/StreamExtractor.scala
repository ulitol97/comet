package org.ragna.comet
package stream.extractors

import data.DataFormat
import exception.stream.timed.StreamTimeoutException
import implicits.RDFElementImplicits
import model.rdf.RDFElement
import stream.StreamSource
import stream.extractors.StreamExtractor.Defaults.*
import stream.extractors.StreamExtractor.Errors
import utils.{Fs2StreamOps, Timer}
import validation.Validator.Types.RDFValidationItem
import validation.result.ValidationResult

import cats.effect.*
import cats.effect.unsafe.implicits.global
import es.weso.rdf.jena.RDFAsJenaModel
import es.weso.rdf.{InferenceEngine, NONE}
import fs2.{Pipe, Stream}

import scala.concurrent.duration.*

/**
 * Abstract class for any extractor capable of reaching a data Stream and
 * extracting data from it, then transform this data to RDF items for post processing.
 * Implementations shall define how the initial Stream is pulled
 * from a data source
 *
 * @param format          Format of the RDF data arriving from the Stream,
 *                        the Extractor expects all data items to share format
 * @param inference       Inference of the RDF data arriving from the Stream,
 *                        the Extractor expects all data items to share inference
 * @param concurrentItems Maximum number of items to be extracted and parsed 
 *                        for RDF in parallel
 *                        (set it to 1 for sequential execution, bear in mind that high values won't necessarily 
 *                        translate into performance improvements unless you 
 *                        know what you are doing)
 * @param itemTimeout     Time that this extractor should wait without being
 *                        fed any item before raising a [[StreamTimeoutException]]
 *                        and stopping the Stream,
 *                        this is specially useful for remote data sources
 *                        (i.e.: Kafka streams) that may stop sending data 
 *                        and cause resource starvation if kept open but also
 *                        to stop streams that have become stuck processing an
 *                        item, etc.
 *
 *                        Set to None for no timeout
 * @param toRdfElement    Helper function for converting the incoming items of type
 *                        [[A]] into [[RDFElement]]s (implicit conversions provided
 *                        in [[RDFElementImplicits]])
 * @tparam A Type of the items expected by the Stream
 * @note In the future, this class can be implemented for any
 *       FS2-compatible data source (files, in-memory structures, etc.)
 * @throws StreamTimeoutException If the time between received items exceeds
 *                                the one configured in  [[itemTimeout]]
 */
abstract class StreamExtractor[A](val format: DataFormat,
                                  val inference: InferenceEngine = defaultInferenceEngine,
                                  protected val concurrentItems: Int = defaultConcurrentParsing,
                                  protected val itemTimeout: Option[FiniteDuration] = defaultIncomingItemsTimeout
                                 )
                                 (
                                   implicit private val toRdfElement: A => RDFElement
                                 ) {

  // Ensure user inputs are valid
  checkConfiguration()

  /**
   * The initial [[inputStream]], transformed through [[toDataItems]] to get
   * a stream of RDF Items
   */
  lazy val dataStream: Stream[IO, RDFValidationItem] =
  // Create the base stream of RDF items
    timedInputStream
      .through(toRdfElements) // A => RDF elements
      .through(toDataItems) // RDF Elements => RDF Resources


  /**
   * Stream containing the items as they arrive from the source of data.
   * Implementations shall define how this initial stream is obtained.
   */
  private[extractors] lazy val inputStream: Stream[IO, A]

  /**
   * The [[inputStream]] of items, timed to fail if not fed for a certain duration
   * configured in [[itemTimeout]]
   *
   * This is just a transformation over the [[inputStream]] that must be implemented
   * by non-abstract extractors
   */
  private lazy val timedInputStream: Stream[IO, A] = itemTimeout match {
    case Some(delay) => inputStream.timedStream(delay)
    case None => inputStream
  }

  /**
   * Source of the incoming stream, one of [[StreamSource]]
   */
  val source: StreamSource

  /**
   * Stream transformation function, converting the RDFElements parsed from
   * [[inputStream]] into RDF Data resources.
   */
  private val toDataItems: Pipe[IO, RDFElement, RDFValidationItem] = inputStream => {
    // Map the incoming Stream of Strings and create RDF models from each string
    inputStream.parEvalMap(concurrentItems)(rdfElement =>
      RDFAsJenaModel.fromString(rdfElement.content, format.name, None)
        .attempt
    )
  }

  /**
   * Stream transformation function, converts the items of type [[A]] arriving
   * from the Stream into [[RDFElement]]s
   *
   * The transformation is done via [[toRdfElement]]
   */
  private val toRdfElements: Pipe[IO, A, RDFElement] = inputStream => {
    inputStream.parEvalMap(concurrentItems)(item => IO {
      toRdfElement(item)
    })
  }

  /**
   * Check the user-controlled inputs to this extractor, preventing
   * the creation of it if necessary
   *
   * @throws IllegalArgumentException On invalid extractor parameters
   */
  protected def checkConfiguration(): Unit = {
    require(concurrentItems > 0, Errors.invalidConcurrentItems)
    require(itemTimeout.forall(_ > 0.millis), Errors.invalidItemsTimeout)
  }
}

private[stream] object StreamExtractor {

  /**
   * Default values used as fallbacks when no
   * alternative has been received
   */
  object Defaults {
    /**
     * Default [[InferenceEngine]] to be used when none is supplied to the extractor
     */
    val defaultInferenceEngine: InferenceEngine = NONE

    /**
     * Default amount of items that are parsed into RDF concurrently
     */
    val defaultConcurrentParsing = 10

    /**
     * Default time to wait without receiving items before closing
     * the Stream, defaults to None so that the Stream runs indefinitely
     */
    val defaultIncomingItemsTimeout: Option[FiniteDuration] = None
  }

  /**
   * Error messages for extractors
   */
  private[extractors] object Errors {
    /**
     * Message thrown when the input concurrency is invalid
     */
    val invalidConcurrentItems: String =
      mkExtractorCreationError(Some("Concurrent items must be > 0"))

    /**
     * Message thrown when the input incoming items timeout is negative
     */
    val invalidItemsTimeout: String =
      mkExtractorCreationError(Some("Incoming items timeout must be non-negative"))

    /**
     * Text preceding any error message raised during the creation of the
     * extractor
     */
    private val extractorCreationErrorPrefix: String =
      "Could not create stream extractor from the supplied data"

    /**
     * Method for creating descriptive error messages for errors that arose 
     * during the creation of the extractor
     *
     * @param message Description of the error
     * @return String containing the specific error ([[message]])
     *         preceded by [[extractorCreationErrorPrefix]]
     */
    def mkExtractorCreationError(message: Option[String]): String = {
      val attachedMessage = message.map(msg => s": $msg").getOrElse("")
      s"$extractorCreationErrorPrefix$attachedMessage"
    }
  }
}
