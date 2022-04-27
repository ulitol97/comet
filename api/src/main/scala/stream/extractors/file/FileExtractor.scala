package org.ragna.comet
package stream.extractors.file

import data.DataFormat
import implicits.RDFElementImplicits._
import model.rdf.RDFElement
import stream.StreamSource
import stream.extractors.StreamExtractor
import stream.extractors.file.FileExtractor
import stream.extractors.file.FileExtractor.Defaults

import cats.effect.IO
import cats.implicits.toString
import es.weso.rdf.InferenceEngine
import fs2.io.file.{Files, Path}
import fs2.kafka.KafkaConsumer
import fs2.{Stream, text}

import java.nio.charset.Charset
import java.nio.file
import java.nio.file.{Path => JavaNioPath}
import scala.concurrent.duration.FiniteDuration

/**
 * [[StreamExtractor]] capable of extracting RDF items from a list of Files, each
 * file is expected to contain a single RDF item
 *
 * Inspired by [[https://fs2.io/#/getstarted/example]]
 *
 * @param files           List of files to be processed, represented by their
 *                        paths
 * @param charset         Charset to be used to operate the requested files
 * @param format          Format of the RDF data arriving from the Stream,
 *                        the Extractor expects all data items to share format
 * @param inference       Inference of the RDF data arriving from the Stream,
 *                        the Extractor expects all data items to share inference
 * @param concurrentItems Maximum number of items to be extracted and parsed 
 *                        for RDF in parallel
 *                        (set it to 1 for sequential execution, bear in mind that high values won't necessarily 
 *                        translate into performance improvements unless you 
 *                        know what you are doing)
 * @note [[StreamExtractor]]s type parameter is set to String since data read from
 *       files will be interpreted as Strings
 */
case class FileExtractor
(
  files: Seq[Path],
  charset: Charset = Defaults.defaultCharset,
  override val format: DataFormat,
  override val inference: InferenceEngine = StreamExtractor.Defaults.defaultInferenceEngine,
  override val concurrentItems: Int = StreamExtractor.Defaults.defaultConcurrentParsing,
  override val itemTimeout: Option[FiniteDuration] = None
)
  extends StreamExtractor[String](format, inference, concurrentItems, itemTimeout) {
  /**
   * Get the initial input stream by taking the list of files, reading the bytes
   * in each of them, and decoding them according to [[charset]]
   *
   * @note Parallelism in file reading is attempted via prefetch
   */
  override private[extractors] lazy val inputStream: Stream[IO, String] =
    Stream.emits(files).flatMap(path =>
      Files[IO].readAll(path))
      .prefetchN(concurrentItems)
      .through(text.decodeWithCharset(charset))

  // Override source
  override val source: StreamSource = StreamSource.Files
}

object FileExtractor {

  private[file] object Defaults {
    /**
     * Default charset expected for files
     */
    val defaultCharset: Charset = Charsets.UTF8.value
  }
}
