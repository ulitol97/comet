package org.ragna.comet
package stream.extractors.file

import java.nio.charset.Charset

/**
 * Abstract class representing some Java accepted charsets
 *
 * @note Used as an enum-replacement for Scala 2 syntax
 * @see [[https://stackoverflow.com/a/71206847/9744696]]
 */
sealed abstract class Charsets(val value: Charset)

/**
 * Enumeration of common charsets from Java's API
 */
object Charsets {
  case object ASCII extends Charsets(Charset.forName("ASCII"))

  case object UTF8 extends Charsets(Charset.forName("UTF8"))

  case object UTF16 extends Charsets(Charset.forName("UTF16"))

  case object UTF32 extends Charsets(Charset.forName("UTF32"))

  case object ISO_8859_1 extends Charsets(Charset.forName("ISO-8859-1"))
}
