package org.ragna.comet
package utils

import cats.effect.{FiberIO, IO, Temporal}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.*

/**
 * Utility class capable of delaying the execution of an IO action for a certain
 * duration
 *
 * This timer can be reset or canceled at any moment
 *
 * @param action Action to be executed when timer delay is over
 * @param delay  Wait before executing the action
 * @tparam A Return type of the scheduled action
 */
final case class Timer[A](action: IO[A], delay: FiniteDuration) {
  /**
   * Fiber running the timer action, a reference to this fiber is kept so that
   * it can be cancelled when the timer is reset
   */
  private var actionFiber: Option[FiberIO[A]] = None

  /**
   * The action to be executed by [[actionFiber]], consisting of waiting for
   * [[delay]] and then running the [[action]]
   */
  private val delayedAction: IO[A] = Temporal[IO].sleep(delay) *> action

  /**
   * Start the countdown on this timer, or reset it in case it's already running
   */
  def schedule(): IO[Unit] = {
    for {
      // Cancel currently running fiber
      _ <- cancel()
      startTime <- IO(System.currentTimeMillis())
      // Create new fiber to re-run task and store reference
      fiber <- delayedAction.onCancel(
        IO {
          val endTime = (System.currentTimeMillis() - startTime) / 1000f
          println(s"Timer timeout canceled after: ${endTime}s")
        }).start
      _ = actionFiber = Some(fiber)
      _ <- IO.println(s"SCHEDULED: ${actionFiber.get}")
    } yield ()
  }

  /**
   * Cancel this timer's countdown, preventing its action from executing
   */
  def cancel(): IO[Unit] = {
    if (actionFiber.isDefined) {
      val fiber = actionFiber.get
      for {
        _ <- fiber.cancel
        _ <- fiber.join
        _ <- IO.println(s"CANCELLED: $fiber")
      } yield ()
    } else IO.unit
  }
}