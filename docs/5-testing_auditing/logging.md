---
id: logging
title: Logging
---

# Logging

@APP_NAME@ is a library meant to be used in third-party software projects,
thus the goal of providing a logging mechanism that does not interfere with
@APP_INNER_NAME@'s parent project.

For this purpose, [scala-logging](https://github.com/lightbend/scala-logging)
has been used for several reasons:
1. It just provides a logging **front-end**, meaning the parent project is
in charge of defining a logging mechanism and configuration,
which will be honored by @APP_NAME@.
2. It is a Scala wrapper for the [SLF4J](https://www.slf4j.org/), a mature and
reliable Java logging framework.
3. Provides several macros and utilities to reduce the verbosity of the code
in charge of logging messages.

```scala title="Code example using scala-logging"
// 1. Extend LazyLogging
object Main extends IOApp with LazyLogging {
  override def run(args: List[String]): IO[ExitCode] = {
    // 2. Log anything from anywhere in the class
    logger.debug("Launching comet")
    IO.pure(ExitCode.Success)
  }
}
```