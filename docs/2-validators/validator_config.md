---
id: validator_config
title: Validator configuration
---

# Validator configuration

@APP_NAME@ validators need some manual configuration in order to work.

## Parameters

The following parameters must be passed down to create a _
ValidatorConfiguration_ instance:

### Mandatory parameters

#### Schema

SHaclEX Schema against which the validation will be performed for all items processed by
this validator

#### Trigger

SHaclEX ValidationTrigger against which the validation will be performed for all items
processed by this validator

### Optional parameters

Some parameters needn't be specified by the user because they already have a
default value that makes sense for most cases.

#### Halt on Invalid

- **Purpose**:  Whether if the validator should halt and raise an error when
  some RDF data is invalid or just emit an `INVALID` validation result and keep
  on processing.
- **Default value**: `false`

#### Halt on Errored

- **Purpose**:  Whether if the validator should halt and raise an error when an
  unexpected error occurs or just emit an `ERRORED` validation result and keep
  on processing.
- **Default value**: `false`

#### Concurrent Items

- **Purpose**: Define the maximum number of items than the validator can
  validate in parallel.
- **Default value**: `10`
