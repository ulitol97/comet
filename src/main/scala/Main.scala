package org.ragna.comet

import cats.effect.{IO, IOApp, ExitCode}

/**
 * Demo entry point.
 * Shall initialize the validation, get the validation Stream
 * and print it
 *
 */
object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    IO.pure(ExitCode.Success)
}
