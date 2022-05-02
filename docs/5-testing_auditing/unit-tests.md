---
id: unit-tests
title: Unit tests
---

# Unit tests

@APP_NAME@ comes with some built-in unit tests written with:

- [scalatest](https://www.scalatest.org/): an extensible, widely adopted scala
  testing framework.
- [cats-effect-testing](https://github.com/typelevel/cats-effect-testing):
  Typelevel's compatibility layer between cats-effect and tests frameworks

You may run the tests by running `sbt test` or simply `test` from the SBT shell.

## Validation tests

Test the working of the validators in different scenarios.

### Results

- Goals:
  - Ensure that the underlying RDF validation mechanism works when using either
    ShEx or SHACL schemas.
  
> The testing of the validation mechanism could be considered redundant, since we should be able to trust [SHaclEX](https://www.weso.es/shaclex/) as a validation library. However, SHaclEX is unstable, and it's better to double-check the SHaclEX validator in our streaming context.

- Modus operandi:
  1. RDF data is generated in all accepted formats.
  2. Validation schemas are generated in all accepted engines and formats.
  3. All combinations are used to perform validations, testing that the
     validation output is correct

### Halting

- Goals:
  - Ensure that @APP_NAME@'s validation stream halts when configured to do so
    for either invalid or erroring validations

- Modus operandi: 
  1. Well formatted and badly formatted RDF data is generated
  2. Validation schemas that won't validate the data are generated
  3. The data is validated against the schemas, checking that the correct error
       is thrown for each situation.

### Timeout

- Goals:
  - Ensure that @APP_NAME@'s validation stream forcibly halts according to its
    extractor timeout when no items are received after some time

- Modus operandi:
  1. A trivial validator is run with an unfeasible timeout
  2. Check that a timeout error is thrown when the validator is initiated

## Extractor tests

- Goals:
  - Test that the pre-configured extractors work as intended, regardless of the
    validation results.

- Modus operandi:
  1. Some trivial but valid RDF/Schema combinations are produced and then
    consumed by a validator, but each time the validator will be fed through a
    different extractor to make sure all extractors work the same.