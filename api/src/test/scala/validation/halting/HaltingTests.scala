package org.ragna.comet
package validation.halting

import data.DataFormat._
import exception.stream.validations.{StreamErroredItemException, StreamInvalidItemException}
import schema.ShExSchemaFormat._
import utils.Samples.StreamSamples.mkSingleValidationResult

import cats.effect.testing.scalatest.AsyncIOSpec
import cats.implicits.catsSyntaxEitherId
import org.ragna.comet.validation.ouputs.SchemaTests
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers

/**
 * Test suite checking that the validation mechanism halts when configured to
 * do so for either invalid or erroring validations using ShEx or SHACL schemas
 *
 * The testing goes as follows:
 *
 * - The same RDF data example will be validated against a given schema,
 * though slightly modified to test both invalid and erroring validations
 *
 * Tests are nested as follows to cover all possibilities:
 *
 * - Per expected behaviour (halt on invalid, halt on errored)
 *    - Per Schema type/engine (ShEx, SHACL, etc.)
 *
 * @note We understand as invalid validations those whose result is INVALID,
 *       whereas errored validations are those who cannot be completed due to
 *       an error in the validation process (normally due to bad data/schema
 *       syntax)
 * @note Standard behaviour (not halting on VALID items) is not tested here
 *       since it is already exhaustively tested in [[SchemaTests]]
 */
//noinspection RedundantDefaultArgument
class HaltingTests extends AsyncFreeSpec with AsyncIOSpec with Matchers {

  "Validation halts" - {
    "on INVALID results" - {
      "using ShEx schemas" in {
        mkSingleValidationResult(
          rdfFormat = TURTLE,
          schemaFormat = SHEXC.asRight,
          valid = false,
          haltOnInvalid = true)
          .assertThrows[StreamInvalidItemException]
      }

      "using SHACL schemas" in {
        mkSingleValidationResult(
          rdfFormat = TURTLE,
          schemaFormat = TURTLE.asLeft,
          valid = false,
          haltOnInvalid = true)
          .assertThrows[StreamInvalidItemException]
      }
    }

    "on ERRORED results" - {
      "using ShEx schemas" in {
        mkSingleValidationResult(
          rdfItem = "This is wrong RDF data that will not validate whatsoever",
          rdfFormat = TURTLE,
          schemaFormat = SHEXC.asRight,
          haltOnInvalid = false,
          haltOnError = true,
          None
        )
          .assertThrows[StreamErroredItemException]
      }

      "using SHACL schemas" in {
        mkSingleValidationResult(
          rdfItem = "This is wrong RDF data that will not validate whatsoever",
          rdfFormat = TURTLE,
          schemaFormat = TURTLE.asLeft,
          haltOnInvalid = false,
          haltOnError = true,
          None
        )
          .assertThrows[StreamErroredItemException]
      }
    }
  }
}
