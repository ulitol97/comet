package org.ragna.comet
package stream

/**
 * Abstract class representing the supported stream sources
 *
 * @note Used as an enum-replacement for Scala 2 syntax
 * @see [[https://stackoverflow.com/a/71206847/9744696]]
 */
sealed abstract class StreamSource(val name: String)

/**
 * Enumeration of the Stream Sources supported by the app
 *
 * @note Extensible to any FS2 compatible data source
 *       (Files, in-memory structures, etc.)
 */
object StreamSource {
  /**
   * Stream source for Kafka streams
   */
  case object Kafka extends StreamSource("kafka")

  /**
   * Stream source for in-memory sequences of items
   */
  case object List extends StreamSource("list")

  /**
   * Stream source for a list of items contained in files
   */
  case object Files extends StreamSource("files")
}
