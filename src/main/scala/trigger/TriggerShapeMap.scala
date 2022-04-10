package org.ragna.comet
package trigger

import cats.*
import cats.data.*
import cats.effect.*
import cats.implicits.*
import es.weso.rdf.PrefixMap
import es.weso.schema.{Schema, ShapeMapTrigger, TargetDeclarations, ValidationTrigger as ValidationTriggerW}
import es.weso.shapemaps.ShapeMap

import scala.language.postfixOps

/**
 * Data class representing a validation trigger by shapeMap,
 * as used in ShEx validations
 *
 * @param shapeMapText    ShapeMap contents, as a raw String
 * @param shapeMapFormat  ShapeMap format, needed to interpret the text
 * @param nodesPrefixMap  Prefix map of the RDF data being validated with this trigger,
 *                        it will be autofilled before validation
 * @param shapesPrefixMap Prefix map of the Schema used for validation with this trigger,
 *                        it will be autofilled before validation
 * @note We may need an schema, but not data, this is because data arrives in
 *       Streams and it is not known beforehand until validation
 */
sealed case class TriggerShapeMap(shapeMapText: String,
                                  shapeMapFormat: ShapeMapFormat,
                                  nodesPrefixMap: PrefixMap = PrefixMap.empty,
                                  shapesPrefixMap: PrefixMap = PrefixMap.empty
                                 )
  extends ValidationTrigger {


  override val `type`: TriggerModeType = TriggerModeType.SHAPEMAP

  /**
   * Attempt to create the real ShaclEx ShapeMap model or return a list with
   * the errors occurred
   *
   * @note We reuse the shapes prefix map as the nodes prefix map
   */
  private lazy val shapeMap: Either[List[String], ShapeMap] =
    ShapeMap.fromString(
      shapeMapText,
      shapeMapFormat.name,
      nodesPrefixMap = nodesPrefixMap,
      shapesPrefixMap = shapesPrefixMap
    ).left.map(_.toList)

  override def getValidationTrigger: Either[List[String], ValidationTriggerW] =
    shapeMap.map(ShapeMapTrigger(_))
}
