package org.ragna.comet
package stream.extractors.list


import data.DataFormat
import model.rdf.RDFElement
import stream.StreamSource
import stream.extractors.StreamExtractor

import cats.effect.IO
import cats.implicits.toString
import es.weso.rdf.InferenceEngine
import fs2.Stream
import fs2.io.file.{Files, Path}
import fs2.kafka.KafkaConsumer

import scala.concurrent.duration.*

/**
 * [[StreamExtractor]] capable of extracting RDF items from an in-memory
 * sequence of items
 *
 * @param items           List of elements to be used as input stream
 * @param format          Format of the RDF data arriving from the Stream,
 *                        the Extractor expects all data items to share format
 * @param inference       Inference of the RDF data arriving from the Stream,
 *                        the Extractor expects all data items to share inference
 * @param concurrentItems Maximum number of items to be extracted and parsed 
 *                        for RDF in parallel
 *                        (set it to 1 for sequential execution, bear in mind that high values won't necessarily 
 *                        translate into performance improvements unless you 
 *                        know what you are doing)
 * @param toRdfElement    Helper function for converting the incoming items of type
 *                        [[A]] into [[RDFElement]]s (implicit conversions provided
 *                        in [[RDFElementImplicits]])
 * @tparam A Type of the items contained in the input list
 */
case class ListExtractor[A]
(
  items: Seq[A],
  override val format: DataFormat,
  override val inference: InferenceEngine = StreamExtractor.Defaults.defaultInferenceEngine,
  override val concurrentItems: Int = StreamExtractor.Defaults.defaultConcurrentParsing,
  override val itemTimeout: Option[FiniteDuration] = None
)(implicit private val toRdfElement: A => RDFElement)
  extends StreamExtractor[A](format, inference, concurrentItems, itemTimeout)(toRdfElement) {
  // Override source
  override val source: StreamSource = StreamSource.List

  override private[extractors] lazy val inputStream: Stream[IO, A] =
    Stream.emits(items).covary[IO] // Merely lift the values in the list
}
