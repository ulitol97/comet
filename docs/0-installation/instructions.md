---
id: instructions
title: Instructions
---

# Installing @APP_NAME@

@APP_NAME@ is cross-compiled for:

- Scala `@SCALA_2_VERSION@`
- Scala `@SCALA_3_VERSION@`

To use @APP_NAME@ in [sbt](https://www.scala-sbt.org/),
simply add the following to your `build.sbt` file:

```scala title="Adding @APP_NAME@ (version-agnostic)"
libraryDependencies += "io.github.ulitol97" %% "comet" % "@APP_VERSION@"
```

The library version that matches your Scala version will be selected
automatically, but you may override it using one of these instead:
```scala title="Adding @APP_NAME@ (Scala @SCALA_2_VERSION@)"
libraryDependencies += "io.github.ulitol97" % "comet_2.13" % "@APP_VERSION@
```
```scala title="Adding @APP_NAME@ (Scala @SCALA_3_VERSION@)"
libraryDependencies += "io.github.ulitol97" % "comet_3" % "@APP_VERSION@"
```

## Tech stack

@APP_NAME@ depends on other technologies and libraries to work, mainly:

### Development and runtime environment

- [Java 11 (LTS)](https://openjdk.java.net/projects/jdk/11/)
- [Scala](https://www.scala-lang.org/download/all.html) `@SCALA_2_VERSION@`/`@SCALA_3_VERSION@` (Scala 3 is recommended)
  - <u>For developers</u>: Scala 3 specific syntax must be avoided for
  cross-compilation to work between Scala 2 and Scala 3

### Scala libraries

- [cats-effect 3](https://typelevel.org/cats-effect/)
- [FS2](https://fs2.io/)
- [FS2 Kafka](https://fd4s.github.io/fs2-kafka/)
- [SHaclEX](https://www.weso.es/shaclex/)

## Addendum

@APP_NAME@ is part of a personal master's thesis, and thus I can't guarantee that it
will be actively maintained.

One way or another, feedback and contributions are always welcome 🤘🏼