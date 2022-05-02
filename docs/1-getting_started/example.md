---
id: example
title: Getting started
---

# Basic example

The following is an example showing how to use @APP_NAME@ with:

- A **List Extractor**, containing a list of RDF strings from which to form a
  stream of RDF data.
- A **Validator**, configured with a _Schema_ and a _ValidationTrigger_ to
  produce a stream of validation results.
- A set of code blocks evaluating the final stream items:
  - <u>EvalMap</u>: Print the items at the end of the processing pipeline
  - <u>Error handling</u>: Define error-recovering behaviour

```scala title="Quickstart example"
import data.DataFormat.*
import exception.stream.validations.*
import stream.extractors.list.ListExtractor
import validation.Validator
import validation.configuration.ValidatorConfiguration

import cats.effect.*
import fs2.*

import scala.concurrent.duration.*

/**
 * Basic showcase code:
 * 1. Create a SHaclEX schema and validation trigger to validate the data
 * 2. Create a data extractor, generating RDF data from a pre-defined list
 * 3. Create the validator, giving it:
 *  - A configuration with the Schema and Trigger it will use
 *  - The extractor from which to get its data
 *    4. Start the validation stream, processing the results
 *
 * @note We pretend to have pre-defined functions that generate RDF data, Schemas, etc.
 *       to hide the complexity of generating these
 */
object Main extends IOApp.Simple {

  override def run(args: List[String]): IO[ExitCode] = {

    for {
      // 1. Create a SHaclEX schema and validation trigger:
      schema <- makeSchemaShexIO() // Schema
      trigger = makeTriggerShex() // Trigger

      // 2. Create a data extractor: list extractor
      listExtractor = ListExtractor(
        items = makeRdfItems(), // List of items to be validated
        format = TURTLE
      )

      // 3. Create the validator
      // 3.1 Validator configuration
      validatorConfiguration = ValidatorConfiguration(schema, trigger)
      // 3.2 Validator instance
      validator = Validator(validatorConfiguration, listExtractor)

      // 4. Start the validation stream
      app <- validator.validate // Init
        .evalMap(IO.println) // Print each item
        .handleErrorWith { error => // Handle each error
          Stream.eval(
            error match {
              case timeoutErr: StreamTimeoutException => IO.println("timeout!")
              case invalidErr: StreamInvalidItemException => IO.println("invalid!")
              case erroredErr: StreamErroredItemException => IO.println("errored!")
              case other => IO.println("other throwable!")
            }
          )
        }
        .onFinalize(IO.println("Main finalized")) // Final action, always runs
        .compile.drain

    } yield app
  }
}
```