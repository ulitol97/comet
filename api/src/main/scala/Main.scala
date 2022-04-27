package org.ragna.comet

import cats.effect.{ExitCode, IO, IOApp}
import com.typesafe.scalalogging.LazyLogging

/**
 * Dummy entry point
 */
object Main extends IOApp with LazyLogging {

  override def run(args: List[String]): IO[ExitCode] = {
    logger.debug("Launching comet")
    IO.pure(ExitCode.Success)
  }
}
