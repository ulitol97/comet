package org.ragna.comet

import exception.stream.timed.StreamTimeoutException
import utils.Timer

import cats.effect._
import cats.effect.unsafe.implicits.global
import cats.instances.list._
import cats.syntax.all._
import com.typesafe.scalalogging.LazyLogging
import fs2.{Pipe, Stream}

import scala.concurrent.duration._

/**
 * Package object with implicit classes providing extension methods
 */
package object utils {

  /**
   * Implicit extension methods for IO instances
   *
   * @param io An effectful piece of information
   * @tparam A Type wrapped in IO
   */
  implicit class IODebugOps[A](io: IO[A]) {
    /**
     * Print the given IO to console along with its executing thread, then
     * return the same input value. Used for debugging purposes.
     *
     * @return An IO identical to the input one
     */
    def debug: IO[A] = io.map { ioValue =>
      println(s"${Thread.currentThread().getName} - $ioValue")
      ioValue
    }
  }

  /**
   * Extension methods for durations
   *
   * @param duration Duration being operated on
   */
  implicit class DurationValidationOps(duration: FiniteDuration) {
    /**
     * Check whether a given duration is contained between a given range of time
     *
     * @param min Lower limit of the duration range
     * @param max Upper limit of the duration range
     * @return A boolean indicating if the condition fulfills
     */
    def isBetween(min: FiniteDuration, max: FiniteDuration): Boolean =
      duration > min && duration < max
  }

  /**
   * Extension methods for FS2 streams
   *
   */

  /**
   * Extension methods for FS2 streams iterating over IO
   *
   * @param stream Stream being operated on
   * @tparam O Type of the items contained in the stream
   */
  implicit class Fs2StreamOps[O](stream: Stream[IO, O]) extends LazyLogging {

    /**
     * Configure this stream to fail if not fed for a certain duration
     *
     * @param timeout Time to wait without consuming items before halting
     *                the resulting stream
     * @return A new Stream with the same structure as the input one, 
     *         but configured to halt and throw an
     *         [[StreamTimeoutException]] if no items are received within
     *         a time window equal to [[timeout]]
     */
    def timedStream(timeout: FiniteDuration): Stream[IO, O] =
      stream.through(toTimedStream(timeout))

    /**
     * Stream transformation function, configures this stream to halt and throw
     * [[StreamTimeoutException]] if no items are received within
     * a time window
     *
     * @param timeout Time to wait without consuming items before halting
     *                the resulting stream
     * @note The timing and cancellations is done via a [[Timer]]
     * @note Experimental feature, do not use for comparing very tight time
     *       thresholds
     * @throws StreamTimeoutException If the time between received items exceeds
     *                                [[incomingItemsTimeout]]
     */
    private def toTimedStream(timeout: FiniteDuration): Pipe[IO, O, O] = inputStream => {
      // Create and start timer
      val finalStream = for {
        // Debug message
        _ <- IO {
          logger.info(s"Setting timed stream to $timeout")
        }
        // Deferred to be filled to stop execution
        cancelSignal <- Deferred[IO, Either[Throwable, Unit]]
        // Code chunk filling the cancellation signal with the halting reason (timeout)
        cancelOperation = cancelSignal.complete(Left(StreamTimeoutException(Some(timeout))))
        // Timer with the power to enact the cancel operation
        timer = Timer(cancelOperation, timeout)

        // For each item processed, reset the timer
        // Set the stream to halt when the cancel signal is enabled
        finalStream =
          inputStream.evalTap { _ =>
            timer.schedule()
          }.interruptWhen(cancelSignal)
        // Start timer already
        _ <- timer.schedule()
      } yield finalStream

      // Return newly composed stream
      finalStream.unsafeRunSync()
    }
  }

}
