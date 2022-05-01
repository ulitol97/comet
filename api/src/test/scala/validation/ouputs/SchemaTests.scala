package org.ragna.comet
package validation.ouputs

import data.DataFormat._
import schema.ShExSchemaFormat._
import utils.Samples.StreamSamples.mkSingleValidationResult
import validation.result.ResultStatus._

import cats.effect.testing.scalatest.AsyncIOSpec
import cats.implicits.catsSyntaxEitherId
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
 *       it is unstable, and it's better to double-check the SHaclEX validator in
 *       our streaming context
 */
//noinspection RedundantDefaultArgument
class SchemaTests extends AsyncFreeSpec with AsyncIOSpec with Matchers {

  "ShEx schemas" - {
    "using ShExC syntax" - {
      "validate VALID RDF data" - {
        "in Turtle format" in {
          mkSingleValidationResult(TURTLE, SHEXC.asRight, valid = true)
            .asserting(_.status shouldBe VALID)
        }

        "in JSON-LD format" in {
          mkSingleValidationResult(JSONLD, SHEXC.asRight, valid = true)
            .asserting(_.status shouldBe VALID)
        }

        "in RDF/XML format" in {
          mkSingleValidationResult(RDFXML, SHEXC.asRight, valid = true)
            .asserting(_.status shouldBe VALID)
        }
      }

      "do not validate INVALID RDF data" - {
        "in Turtle format" in {
          mkSingleValidationResult(TURTLE, SHEXC.asRight, valid = false)
            .asserting(_.status shouldBe INVALID)
        }

        "in JSON-LD format" in {
          mkSingleValidationResult(JSONLD, SHEXC.asRight, valid = false)
            .asserting(_.status shouldBe INVALID)
        }

        "in RDF/XML format" in {
          mkSingleValidationResult(RDFXML, SHEXC.asRight, valid = false)
            .asserting(_.status shouldBe INVALID)
        }
      }
    }

  }

  "SHACL schemas" - {
    "using TURTLE syntax" - {
      "validate VALID RDF data" - {
        "in Turtle format" in {
          mkSingleValidationResult(rdfFormat = TURTLE, schemaFormat = TURTLE.asLeft, valid = true)
            .asserting(_.status shouldBe VALID)
        }

        "in JSON-LD format" in {
          mkSingleValidationResult(rdfFormat = JSONLD, schemaFormat = TURTLE.asLeft, valid = true)
            .asserting(_.status shouldBe VALID)
        }

        "in RDF/XML format" in {
          mkSingleValidationResult(rdfFormat = RDFXML, schemaFormat = TURTLE.asLeft, valid = true)
            .asserting(_.status shouldBe VALID)
        }
      }

      "do not validate INVALID RDF data" - {
        "in Turtle format" in {
          mkSingleValidationResult(rdfFormat = TURTLE, schemaFormat = TURTLE.asLeft, valid = false)
            .asserting(_.status shouldBe INVALID)
        }

        "in JSON-LD format" in {
          mkSingleValidationResult(rdfFormat = JSONLD, schemaFormat = TURTLE.asLeft, valid = false)
            .asserting(_.status shouldBe INVALID)
        }

        "in RDF/XML format" in {
          mkSingleValidationResult(rdfFormat = RDFXML, schemaFormat = TURTLE.asLeft, valid = false)
            .asserting(_.status shouldBe INVALID)
        }
      }
    }

    "using JSON-LD syntax" - {
      "validate VALID RDF data" - {
        "in Turtle format" in {
          mkSingleValidationResult(rdfFormat = TURTLE, schemaFormat = JSONLD.asLeft, valid = true)
            .asserting(_.status shouldBe VALID)
        }

        "in JSON-LD format" in {
          mkSingleValidationResult(rdfFormat = JSONLD, schemaFormat = JSONLD.asLeft, valid = true)
            .asserting(_.status shouldBe VALID)
        }

        "in RDF/XML format" in {
          mkSingleValidationResult(rdfFormat = RDFXML, schemaFormat = JSONLD.asLeft, valid = true)
            .asserting(_.status shouldBe VALID)
        }
      }

      "do not validate INVALID RDF data" - {
        "in Turtle format" in {
          mkSingleValidationResult(rdfFormat = TURTLE, schemaFormat = JSONLD.asLeft, valid = false)
            .asserting(_.status shouldBe INVALID)
        }

        "in JSON-LD format" in {
          mkSingleValidationResult(rdfFormat = JSONLD, schemaFormat = JSONLD.asLeft, valid = false)
            .asserting(_.status shouldBe INVALID)
        }

        "in RDF/XML format" in {
          mkSingleValidationResult(rdfFormat = RDFXML, schemaFormat = JSONLD.asLeft, valid = false)
            .asserting(_.status shouldBe INVALID)
        }
      }
    }

    "using RDF/XML syntax" - {
      "validate VALID RDF data" - {
        "in Turtle format" in {
          mkSingleValidationResult(rdfFormat = TURTLE, schemaFormat = RDFXML.asLeft, valid = true)
            .asserting(_.status shouldBe VALID)
        }

        "in JSON-LD format" in {
          mkSingleValidationResult(rdfFormat = JSONLD, schemaFormat = RDFXML.asLeft, valid = true)
            .asserting(_.status shouldBe VALID)
        }

        "in RDF/XML format" in {
          mkSingleValidationResult(rdfFormat = RDFXML, schemaFormat = RDFXML.asLeft, valid = true)
            .asserting(_.status shouldBe VALID)
        }
      }

      "do not validate INVALID RDF data" - {
        "in Turtle format" in {
          mkSingleValidationResult(rdfFormat = TURTLE, schemaFormat = RDFXML.asLeft, valid = false)
            .asserting(_.status shouldBe INVALID)
        }

        "in JSON-LD format" in {
          mkSingleValidationResult(rdfFormat = JSONLD, schemaFormat = RDFXML.asLeft, valid = false)
            .asserting(_.status shouldBe INVALID)
        }

        "in RDF/XML format" in {
          mkSingleValidationResult(rdfFormat = RDFXML, schemaFormat = RDFXML.asLeft, valid = false)
            .asserting(_.status shouldBe INVALID)
        }
      }
    }
  }
}
