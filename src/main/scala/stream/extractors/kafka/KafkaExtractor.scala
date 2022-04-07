package org.ragna.comet
package stream.extractors.kafka

import data.DataFormat
import model.rdf.RDFElement
import stream.StreamSource
import stream.extractors.StreamExtractor

import cats.effect.{IO, Temporal}
import cats.implicits.*
import es.weso.rdf.InferenceEngine
import fs2.Stream
import fs2.kafka.*
import org.apache.kafka.common.KafkaException

import scala.concurrent.duration.*

/**
 * [[StreamExtractor]] capable of extracting RDF items from an Apache Kafka
 * stream of items
 *
 *
 *
 * Kafka streams are operated as FS2 streams thanks to the library FS2-Kafka
 *
 * @param configuration     Configuration supplied to this extractor to interact
 *                          with a Kafka stream
 * @param format            Format of the RDF data arriving from the Stream,
 *                          the Extractor expects all data items to share format
 * @param inference         Inference of the RDF data arriving from the Stream,
 *                          the Extractor expects all data items to share inference
 * @param concurrentItems   Maximum number of items to be parsed for RDF in parallel
 *                          (to parallelize the pulling of items from Kafka, see
 *                          [[configuration.concurrentItems]])
 * @param keyDeserializer   Deserializer used to map the incoming stream keys
 *                          to instances of type [[K]]
 * @param valueDeserializer Deserializer used to map the incoming stream keys
 *                          to instances of type [[V]]
 * @tparam K Type to which kafka-stream keys are deserialized
 * @tparam V Type to which kafka-stream values are deserialized
 * @note Although incoming stream items can be deserialized
 *       into any format [[V]] (as long as a [[KafkaDeserializer]]
 *       is provided for type [[V]]),
 *       they'll eventually be converted to [[RDFElement]]s in order to
 *       parse RDF from them
 * @see Apache Kafka: [[https://kafka.apache.org/]]
 * @see FS2-Kafka: [[https://fd4s.github.io/fs2-kafka/]]
 * @throws KafkaException When the underlying Kafka consumer cannot be built
 *                        or the connection fails
 */
case class KafkaExtractor[K, V]
(
  configuration: KafkaExtractorConfiguration,
  override val format: DataFormat,
  override val inference: InferenceEngine = StreamExtractor.Defaults.defaultInferenceEngine,
  override val concurrentItems: Int = StreamExtractor.Defaults.defaultConcurrentParsing,
  override val itemTimeout: Option[FiniteDuration] = KafkaExtractor.Defaults.defaultIncomingItemsTimeout
)(
  implicit private val keyDeserializer: Deserializer[IO, K],
  private val valueDeserializer: Deserializer[IO, V],
  private val toRdfElement: V => RDFElement
)
  extends StreamExtractor[V](format, inference, concurrentItems, itemTimeout)(toRdfElement) {

  /**
   * FS2 representation of the input Stream coming from Kafka, with its
   * items being forcibly turned to Strings whatever their type (see class notes)
   *
   * Subscribes to the input topic to extract records, which are processed in
   * parallel although results are still emitted according to the order of 
   * incoming elements
   *
   */
  override private[extractors] lazy val inputStream: Stream[IO, V] = KafkaConsumer.stream(consumerSettings)
    .subscribeTo(configuration.topic)
    .records
    .mapAsync(configuration.concurrentItems) { committable =>
      // Merely extract the record value
      IO.pure(committable.record.value)
    }

  // Override source
  override val source: StreamSource = StreamSource.Kafka


  /**
   * Settings for the [[KafkaConsumer]] pulling items from the Kafka stream,
   * build from this extractor's [[configuration]]
   */
  private val consumerSettings: ConsumerSettings[IO, K, V] =
    ConsumerSettings(keyDeserializer, valueDeserializer)
      .withBootstrapServers(configuration.bootstrapServers)
      .withGroupId(configuration.groupId)
      .withSessionTimeout(configuration.sessionTimeout)
      .withAutoOffsetReset(configuration.offsetReset)
      .withCommitRecovery(configuration.commitRecovery)
      .withCommitTimeout(configuration.commitTimeout)
      .withEnableAutoCommit(configuration.autoCommit)
      .withAutoCommitInterval(configuration.commitInterval)
      .withPollTimeout(configuration.pollTimeout)
      .withPollInterval(configuration.pollInterval)


  override protected def checkConfiguration(): Unit = {
    // Parent checks
    super.checkConfiguration()
    // Configuration checks
    configuration.testConfiguration()
  }
}

object KafkaExtractor {

  /**
   * Default values used as fallbacks when no
   * alternative has been received
   */
  private[kafka] object Defaults {
    /**
     * Default time to wait without receiving items before closing
     * the Stream, for Kafka it is defined by default so that the app
     * is not indefinitely waiting for data
     */
    val defaultIncomingItemsTimeout: Option[FiniteDuration] = Some(30.seconds)
  }
}
