package org.ragna.comet
package validation.result

import es.weso.schema.{Result => ValidationReport}

/**
 * Abstract class representing the status a validation result can have
 *
 * @param textValue Textual representation of this status
 * @param valid Whether if this status represents a successful validation
 * @note Used as an enum-replacement for Scala 2 syntax
 * @see [[https://stackoverflow.com/a/71206847/9744696]]
 */
sealed abstract class ResultStatus(val textValue: String, val valid: Boolean) {
  override def toString: String = textValue
}

/**
 * Enum with the different result statuses acknowledged by the app
 */
object ResultStatus {
  /**
   * Shortcut for extracting the result status from a Validation result
   *
   * @param report Input result
   * @return An [[ERRORED]] status if results are missing, or a 
   *         [[VALID]]/[[INVALID]] status depending on the input state
   */
  def fromValidationReport(report: Option[ValidationReport]): ResultStatus =
    report match {
      case Some(r) => if (r.isValid) VALID else INVALID
      case None => ERRORED
    }

  /**
   * Data could be validated and turned out VALID
   */
  case object VALID extends ResultStatus("valid", true)

  /**
   * Data could be validated and turned out INVALID
   */
  case object INVALID extends ResultStatus("invalid", false)

  /**
   * Data could not be validated, errors arose during the validation process
   */
  case object ERRORED extends ResultStatus("errored", false)
}
