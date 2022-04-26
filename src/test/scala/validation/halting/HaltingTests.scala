package org.ragna.comet
package validation.halting

import data.DataFormat
import data.DataFormat.*
import exception.stream.validations.{StreamErroredItemException, StreamInvalidItemException}
import implicits.RDFElementImplicits.rdfFromString
import schema.ShExSchemaFormat
import schema.ShExSchemaFormat.*
import stream.extractors.StreamExtractor
import stream.extractors.list.ListExtractor
import trigger.ShapeMapFormat.*
import trigger.TriggerModeType.{SHAPEMAP, TARGET_DECLARATIONS}
import trigger.{ShapeMapFormat, TriggerModeType, ValidationTrigger}
import utils.Samples.StreamSamples.mkSingleValidationResult
import validation.Validator
import validation.configuration.ValidatorConfiguration
import validation.ouputs.SchemaTests
import validation.result.ResultStatus.*
import validation.result.ValidationResult

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import es.weso.schema.Schema
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
 *       an error in the validation process (nomally due to bad data/schema
 *       syntax)
 * @note Standard behaviour (not halting on VALID items) is not tested here
 *       since it is already exhaustively tested in [[SchemaTests]]
 */
//noinspection RedundantDefaultArgument
class HaltingTests extends AsyncFreeSpec with AsyncIOSpec with Matchers {

  "Validation halts on INVALID" - {
    "using ShEx schemas" in {
      mkSingleValidationResult(
        rdfFormat = TURTLE,
        schemaFormat = SHEXC,
        valid = false,
        haltOnInvalid = true)
        .assertThrows[StreamInvalidItemException]
    }

    "using SHACL schemas" in {
      mkSingleValidationResult(
        rdfFormat = TURTLE,
        schemaFormat = TURTLE,
        valid = false,
        haltOnInvalid = true)
        .assertThrows[StreamInvalidItemException]
    }
  }

  "Validation halts on ERRORED" - {
    "using ShEx schemas" in {
      mkSingleValidationResult(
        rdfItem = "This is wrong RDF data that will not validate whatsoever",
        rdfFormat = TURTLE,
        schemaFormat = SHEXC,
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
        schemaFormat = TURTLE,
        haltOnInvalid = false,
        haltOnError = true,
        None
      )
        .assertThrows[StreamErroredItemException]
    }
  }
}
