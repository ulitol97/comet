package org.ragna.comet
package stream

/**
 * Defines the Stream Sources supported by the app
 *
 * @note Extensible to any FS2 compatible data source
 *       (Files, in-memory structures, etc.)
 */
enum StreamSource(val name: String) {
  /**
   * Stream source for Kafka streams
   */
  case Kafka extends StreamSource("kafka")

  /**
   * Stream source for in-memory sequences of items
   */
  case List extends StreamSource("list")

  /**
   * Stream source for a list of items contained in files
   */
  case Files extends StreamSource("files")
}
