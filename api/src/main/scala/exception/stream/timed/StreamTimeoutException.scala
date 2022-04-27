package org.ragna.comet
package exception.stream.timed

import scala.concurrent.duration.FiniteDuration


/** Custom exception thrown when a Stream has not received any items after
 * a certain time interval, causing it to halt
 *
 * @note No message/cause is provided since this exception is manually thrown based
 *       on user configurations of time intervals
 */
case class StreamTimeoutException(timeoutInterval: Option[FiniteDuration]) extends
  RuntimeException(StreamTimeoutException.mkMessage(timeoutInterval), null)

object StreamTimeoutException {
  /** Fixed message preceding the exception message
   */
  private[timed] val prefix =
    "The stream halted since no items where received in the specified time interval"

  /** Fixed message preceding the exception message
   */
  private[timed] def mkMessage(interval: Option[FiniteDuration]) = {
    val durationStr = interval.map(d => s" ($d)").getOrElse("")
    s"$prefix$durationStr"
  }
}
