---
id: predefined_errors
title: Pre-defined errors
---

# Pre-defined errors

@APP_NAME@ has some built-in exception types that are thrown on some common
error that might arise during the validation flow.

## Stream exceptions

Errors occurred whilst running the validation stream:

### Validation

An error occurred where a validation output does not comply with the validator's
configuration:

- [StreamInvalidItemException](https://ulitol97.github.io/comet/scaladoc/org/ragna/comet/exception/stream/validations/StreamInvalidItemException.html):
  Thrown when an item did not validate against its validation schema and the
  validator was configured to halt on invalid data.
- [StreamErroredItemException](https://ulitol97.github.io/comet/scaladoc/org/ragna/comet/exception/stream/validations/StreamErroredItemException.html):
  Thrown when the processing of an item threw an error and the validator was
  configured to halt on errors. Contains the original error cause.

### Timed

- [StreamTimeoutException](https://ulitol97.github.io/comet/scaladoc/org/ragna/comet/exception/stream/timed/StreamTimeoutException.html):
  Thrown when the validation stream was not fed any data for a time period
  longer than the extractor's configured timeout.

---

All stream-related custom exceptions can be found in:

- [org.ragna.comet.exception.stream.timed](https://ulitol97.github.io/comet/scaladoc/org/ragna/comet/exception/stream/timed.html)
- [org.ragna.comet.exception.stream.validations](https://ulitol97.github.io/comet/scaladoc/org/ragna/comet/exception/stream/validations.html)

## Configuration exceptions

- [IllegalArgumentException](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/IllegalArgumentException.html):
  Thrown when the validators/extractors are configured
  with invalid parameters.