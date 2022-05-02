---
id: validator_results
title: Validation results
---

# Validation results

@APP_NAME@ validators emit a stream
of _[ValidationResult](https://ulitol97.github.io/comet/scaladoc/org/ragna/comet/validation/result/ValidationResult.html)_
instances.

Each result contains a:

- _[ResultStatus](https://ulitol97.github.io/comet/scaladoc/org/ragna/comet/validation/result/ResultStatus.html)_:
  represents whether if the validation was successful, unsuccessful or couldn't
  be performed.

- _result_: the inner _ValidationReport_, as returned by SHaclEX (if available).
  Will be empty if the validation could not complete.