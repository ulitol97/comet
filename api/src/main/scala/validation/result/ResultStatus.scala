package org.ragna.comet
package validation.result

import validation.result.ResultStatus.{ERRORED, INVALID}

import es.weso.schema.Result as ValidationReport

/**
 * Enum with the different result statuses acknowledged by the app
 *
 */
enum ResultStatus {
  /**
   * Data could be validated and turned out VALID
   */
  case VALID

  /**
   * Data could be validated and turned out INVALID
   */
  case INVALID

  /**
   * Data could not be validated, errors arose during the validation process
   */
  case ERRORED
}

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
}
