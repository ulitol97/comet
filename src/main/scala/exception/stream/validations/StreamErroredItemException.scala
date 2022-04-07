package org.ragna.comet
package exception.stream.validations

import exception.stream.*
import validation.result.ResultStatus.*

import es.weso.schema.Result as ValidationReport


/** Custom exception thrown when a failure occurs while
 * validating a Stream of items because an item parsing and/or validation
 * raised an unhandled error
 *
 * The underlying status will be [[ERRORED]] and no validation result will be
 * attached
 */
case class StreamErroredItemException(
                                       override val message: String,
                                       override val cause: Throwable
                                     )
  extends StreamValidationException(message, cause, ERRORED, None)

object StreamErroredItemException
  extends StreamValidationExceptionCompanion[StreamErroredItemException] {
  override val prefix: String =
    "Stream halted because an error occurred while validating an item"

  def apply(message: String = prefix, cause: Option[Throwable] = None): StreamErroredItemException =
    new StreamErroredItemException(message, cause.orNull)
}

