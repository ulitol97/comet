package org.ragna.comet
package trigger

import es.weso.schema.{ValidationTrigger => ValidationTriggerW}

/**
 * Common trait to all validation triggers handled by the library
 *
 * This is parent to several adapter classes trying to bridge the functionality
 * in [[ValidationTriggerW]] with this library
 */
trait ValidationTrigger {

  /** Corresponding type of this validation trigger inside [[ValidationTriggerW]]
   */
  val `type`: TriggerModeType

  /** Get a final [[ValidationTrigger]], which is used internally for schema validations,
   * from the data inside this object
   *
   * @return The [[ValidationTrigger]] logical model as used by WESO libraries,
   *         or the list of errors occurred while creating it
   */
  def getValidationTrigger: Either[List[String], ValidationTriggerW]

}
