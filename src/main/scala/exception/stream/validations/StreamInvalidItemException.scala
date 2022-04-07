package org.ragna.comet
package exception.stream.validations

import exception.stream.*
import validation.result.ResultStatus.*

import es.weso.schema.Result as ValidationReport


/** Custom exception thrown when a failure occurs while
 * validating a Stream of items because an item validation failed
 *
 * The underlying status will be [[INVALID]] and the invalid validation 
 * result will be attached
 */
case class StreamInvalidItemException(override val message: String,
                                      override val cause: Throwable,
                                      override val reason: Option[ValidationReport])
  extends StreamValidationException(message, cause, INVALID, reason)

object StreamInvalidItemException
  extends StreamValidationExceptionCompanion[StreamInvalidItemException] {
  override val prefix: String = "Stream halted because an item was invalid"

  def apply(
             message: String = prefix,
             cause: Option[Throwable] = None,
             reason: Option[ValidationReport] = None
           ): StreamInvalidItemException =
    new StreamInvalidItemException(message, cause.orNull, reason)
}



