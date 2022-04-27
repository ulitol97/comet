package org.ragna.comet
package trigger

import es.weso.shapemaps.{Compact, JsonShapeMapFormat}


/** Enumeration of the different formats a ShapeMap can take,
 * based on those formats specified in ShaclEx
 */
enum ShapeMapFormat(val name: String) {
  case COMPACT extends ShapeMapFormat(Compact.name)
  case JSON extends ShapeMapFormat(JsonShapeMapFormat.name)
}
