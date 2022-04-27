package org.ragna.comet
package trigger

/**
 * Abstract class representing the available Validation Triggers
 *
 * @note Used as an enum-replacement for Scala 2 syntax
 * @see [[https://stackoverflow.com/a/71206847/9744696]]
 */
sealed abstract class TriggerModeType(val name: String)

/** Enumeration of the different types of Validation Triggers that can be
 * found accompanying validation schemas
 */
object TriggerModeType {
  /**
   * ShEx validation is performed using a ShapeMap
   */
  case object SHAPEMAP extends TriggerModeType("ShapeMap")

  /**
   * SHACL validation is performed specifying target declarations
   */
  case object TARGET_DECLARATIONS extends TriggerModeType("TargetDecls")
}
