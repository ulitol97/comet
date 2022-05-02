---
id: validator
title: Validation system
---

# Validation system

[Validators](https://ulitol97.github.io/comet/scaladoc/org/ragna/comet/validation/Validator.html)
are @APP_NAME@'s core. A validator instance produces a stream of validation
results which can be further processed if necessary.

The following are required to create a validator instance:

1.
A _[ValidatorConfiguration](https://ulitol97.github.io/comet/scaladoc/org/ragna/comet/validation/configuration/ValidatorConfiguration.html)_:
contains the information on how the validator must perform validations and when
it should stop.
2.
A _[StreamExtractor](https://ulitol97.github.io/comet/scaladoc/org/ragna/comet/stream/extractors/StreamExtractor.html)_:
reference to the extractor from which the validator will get its input RDF data
stream. This input data stream is transformed to a results stream by validating
each incoming piece of data.
3. A template type:
   type of the items expected by the extractor (see next sections)

> Signature of the Validator class:
> ```scala
> class Validator[A](
>  configuration: ValidatorConfiguration,
>  private val extractor: StreamExtractor[A]
> )
> ```

Once created, simply invoke `Validator#validate` to get the results stream.