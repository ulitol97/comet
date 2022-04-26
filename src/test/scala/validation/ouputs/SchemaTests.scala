package org.ragna.comet
package validation.ouputs

import data.DataFormat
import data.DataFormat.*
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
import validation.result.ResultStatus.*
import validation.result.ValidationResult

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import es.weso.schema.Schema
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers

/**
 * Test suite checking that the validation mechanism works when using either 
 * ShEx or SHACL schemas
 *
 * The testing goes as follows:
 *
 * - The same RDF data example will be validated against a given schema,
 * though slightly modified to test both valid and invalid validations
 *
 * Tests are nested as follows to cover all possibilities:
 *
 * - Per Schema type/engine (ShEx, SHACL, etc.)
 *    - Per Schema syntax
 *      - Per expected validation result (valid or invalid)
 *        - Per input RDF data format (Turtle, JSON-LD, RDF/XML...)
 *
 * @note The testing of the validation mechanism could be considered redundant,
 *       since we should be able to trust SHaclEX as a validation library
 *
 *       Still,
 *       it is unstable and its better to double check the SHaclEX validator in
 *       our streaming context
 */
//noinspection RedundantDefaultArgument
class SchemaTests extends AsyncFreeSpec with AsyncIOSpec with Matchers {

  "ShEx schemas" - {
    "using ShExC syntax" - {
      "validate VALID RDF data" - {
        "in Turtle format" in {
          mkSingleValidationResult(TURTLE, SHEXC, valid = true)
            .asserting(_.status shouldBe VALID)
        }

        "in JSON-LD format" in {
          mkSingleValidationResult(JSONLD, SHEXC, valid = true)
            .asserting(_.status shouldBe VALID)
        }

        "in RDF/XML format" in {
          mkSingleValidationResult(RDFXML, SHEXC, valid = true)
            .asserting(_.status shouldBe VALID)
        }
      }

      "do not validate INVALID RDF data" - {
        "in Turtle format" in {
          mkSingleValidationResult(TURTLE, SHEXC, valid = false)
            .asserting(_.status shouldBe INVALID)
        }

        "in JSON-LD format" in {
          mkSingleValidationResult(JSONLD, SHEXC, valid = false)
            .asserting(_.status shouldBe INVALID)
        }

        "in RDF/XML format" in {
          mkSingleValidationResult(RDFXML, SHEXC, valid = false)
            .asserting(_.status shouldBe INVALID)
        }
      }
    }

  }

  "SHACL schemas" - {
    "using TURTLE syntax" - {
      "validate VALID RDF data" - {
        "in Turtle format" in {
          mkSingleValidationResult(rdfFormat = TURTLE, schemaFormat = TURTLE, valid = true)
            .asserting(_.status shouldBe VALID)
        }

        "in JSON-LD format" in {
          mkSingleValidationResult(rdfFormat = JSONLD, schemaFormat = TURTLE, valid = true)
            .asserting(_.status shouldBe VALID)
        }

        "in RDF/XML format" in {
          mkSingleValidationResult(rdfFormat = RDFXML, schemaFormat = TURTLE, valid = true)
            .asserting(_.status shouldBe VALID)
        }
      }

      "do not validate INVALID RDF data" - {
        "in Turtle format" in {
          mkSingleValidationResult(rdfFormat = TURTLE, schemaFormat = TURTLE, valid = false)
            .asserting(_.status shouldBe INVALID)
        }

        "in JSON-LD format" in {
          mkSingleValidationResult(rdfFormat = JSONLD, schemaFormat = TURTLE, valid = false)
            .asserting(_.status shouldBe INVALID)
        }

        "in RDF/XML format" in {
          mkSingleValidationResult(rdfFormat = RDFXML, schemaFormat = TURTLE, valid = false)
            .asserting(_.status shouldBe INVALID)
        }
      }
    }

    "using JSON-LD syntax" - {
      "validate VALID RDF data" - {
        "in Turtle format" in {
          mkSingleValidationResult(rdfFormat = TURTLE, schemaFormat = JSONLD, valid = true)
            .asserting(_.status shouldBe VALID)
        }

        "in JSON-LD format" in {
          mkSingleValidationResult(rdfFormat = JSONLD, schemaFormat = JSONLD, valid = true)
            .asserting(_.status shouldBe VALID)
        }

        "in RDF/XML format" in {
          mkSingleValidationResult(rdfFormat = RDFXML, schemaFormat = JSONLD, valid = true)
            .asserting(_.status shouldBe VALID)
        }
      }

      "do not validate INVALID RDF data" - {
        "in Turtle format" in {
          mkSingleValidationResult(rdfFormat = TURTLE, schemaFormat = JSONLD, valid = false)
            .asserting(_.status shouldBe INVALID)
        }

        "in JSON-LD format" in {
          mkSingleValidationResult(rdfFormat = JSONLD, schemaFormat = JSONLD, valid = false)
            .asserting(_.status shouldBe INVALID)
        }

        "in RDF/XML format" in {
          mkSingleValidationResult(rdfFormat = RDFXML, schemaFormat = JSONLD, valid = false)
            .asserting(_.status shouldBe INVALID)
        }
      }
    }

    "using RDF/XML syntax" - {
      "validate VALID RDF data" - {
        "in Turtle format" in {
          mkSingleValidationResult(rdfFormat = TURTLE, schemaFormat = RDFXML, valid = true)
            .asserting(_.status shouldBe VALID)
        }

        "in JSON-LD format" in {
          mkSingleValidationResult(rdfFormat = JSONLD, schemaFormat = RDFXML, valid = true)
            .asserting(_.status shouldBe VALID)
        }

        "in RDF/XML format" in {
          mkSingleValidationResult(rdfFormat = RDFXML, schemaFormat = RDFXML, valid = true)
            .asserting(_.status shouldBe VALID)
        }
      }

      "do not validate INVALID RDF data" - {
        "in Turtle format" in {
          mkSingleValidationResult(rdfFormat = TURTLE, schemaFormat = RDFXML, valid = false)
            .asserting(_.status shouldBe INVALID)
        }

        "in JSON-LD format" in {
          mkSingleValidationResult(rdfFormat = JSONLD, schemaFormat = RDFXML, valid = false)
            .asserting(_.status shouldBe INVALID)
        }

        "in RDF/XML format" in {
          mkSingleValidationResult(rdfFormat = RDFXML, schemaFormat = RDFXML, valid = false)
            .asserting(_.status shouldBe INVALID)
        }
      }
    }
  }
}
