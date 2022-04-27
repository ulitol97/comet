package org.ragna.comet
package trigger

import es.weso.schema.{Schema, TargetDeclarations, ValidationTrigger => ValidationTriggerW}


/** Data class representing a validation trigger by target declarations,
 * as used in SHACL validations
 *
 * It needs no additional information, everything can be extracted from their
 * companion Data and Schema
 */
sealed case class TriggerTargetDeclarations()
  extends ValidationTrigger {

  override val `type`: TriggerModeType = TriggerModeType.TARGET_DECLARATIONS

  override def getValidationTrigger: Either[List[String], ValidationTriggerW] =
    Right(TargetDeclarations)
}
