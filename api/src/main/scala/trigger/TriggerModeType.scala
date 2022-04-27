package org.ragna.comet
package trigger

/** Enumeration of the different types of Validation Triggers that can be
 * found accompanying validation schemas
 */
enum TriggerModeType(val name: String) {
  case SHAPEMAP extends TriggerModeType("ShapeMap")
  case TARGET_DECLARATIONS extends TriggerModeType("TargetDecls")
}
