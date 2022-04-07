package org.ragna.comet
package stream.extractors.file

import java.nio.charset.Charset


/**
 * Shortcut to access some common charsets from Java's API
 */
enum Charsets(val value: Charset) {
  case ASCII extends Charsets(Charset.forName("ASCII"))
  case UTF8 extends Charsets(Charset.forName("UTF8"))
  case UTF16 extends Charsets(Charset.forName("UTF16"))
  case UTF32 extends Charsets(Charset.forName("UTF32"))
  case ISO_8859_1 extends Charsets(Charset.forName("ISO-8859-1"))
}
