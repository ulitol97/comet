package org.ragna.comet
package trigger

import es.weso.shapemaps.{Compact, JsonShapeMapFormat}

/**
 * Abstract class representing the available formats for ShapeMaps
 *
 * @note Used as an enum-replacement for Scala 2 syntax
 * @see [[https://stackoverflow.com/a/71206847/9744696]]
 */
sealed abstract class ShapeMapFormat(val name: String)

/** Enumeration of the different formats a ShapeMap can take,
 * based on those formats specified in ShaclEx
 */
object ShapeMapFormat {
  /**
   * ShapeMap follows "compact" syntax
   */
  case object COMPACT extends ShapeMapFormat(Compact.name)

  /**
   * ShapeMap follows JSON syntax
   */
  case object JSON extends ShapeMapFormat(JsonShapeMapFormat.name)
}
