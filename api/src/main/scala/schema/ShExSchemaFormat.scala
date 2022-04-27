package org.ragna.comet
package schema

/**
 * Abstract class representing the available formats for ShEx schemas
 *
 * @note Used as an enum-replacement for Scala 2 syntax
 * @see [[https://stackoverflow.com/a/71206847/9744696]]
 */
sealed abstract class ShExSchemaFormat(val name: String)

/**
 * Enumeration of the schema formats accepted by the application for ShEx schemas
 */
object ShExSchemaFormat {
  /**
   * Schema uses "shexc" syntax
   */
  case object SHEXC extends ShExSchemaFormat("ShExC")

  /**
   * Schema uses JSON syntax
   */
  case object SHEXJ extends ShExSchemaFormat("ShExJ")
}
