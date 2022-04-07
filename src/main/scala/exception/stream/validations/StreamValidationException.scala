package org.ragna.comet
package exception.stream.validations

import exception.stream.validations.{StreamValidationException, StreamValidationExceptionCompanion}
import validation.result.ResultStatus

import es.weso.schema.Result as ValidationReport


/** Abstract class covering custom exceptions thrown when a failure occurs while
 * validating a Stream of items
 *
 * @param message Reason/explanation of why the exception occurred
 * @param cause   Nested exception that caused the the validation to fail
 * @param status  Status of the item that caused the exception
 * @param reason  For invalid items, the validation report of the failing
 *                item that caused the exception
 */
abstract class StreamValidationException(
                                          val message: String,
                                          val cause: Throwable,
                                          val status: ResultStatus,
                                          val reason: Option[ValidationReport]
                                        ) extends Exception(message, cause)

object StreamValidationException
  extends StreamValidationExceptionCompanion[StreamValidationException]

private[exception] trait StreamValidationExceptionCompanion[E <: StreamValidationException] {

  /** Fixed message preceding the exception message
   */
  val prefix = "An error occurred while validating the data stream"

  /**
   * Helper to make error messages from [[prefix]]
   *
   * @param originalMessage Message to be attached to the exception
   * @return New error message with [[prefix]] and [[originalMessage]]
   */
  protected def mkMessage(originalMessage: String): String = s"$prefix: $originalMessage"
}
