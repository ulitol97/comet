package org.ragna.comet
package validation.timeout

import data.DataFormat
import data.DataFormat.*
import exception.stream.timed.StreamTimeoutException
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

import scala.concurrent.duration.*

/**
 * Test suite checking that the validation mechanism forcibly halts according
 * to its extractor timeout when no items are received for some time
 *
 * The testing goes as follows:
 *
 * - A single trivial validator will be run with a custom item timeout and
 * then checked for timeout exceptions
 */
//noinspection RedundantDefaultArgument
class TimeoutTests extends AsyncFreeSpec with AsyncIOSpec with Matchers {
  "Validation halts on extractor TIMEOUT" in {
    // Generate valid data, although with a minimal unsatisfiable timeout
    mkSingleValidationResult(
      rdfFormat = TURTLE,
      schemaFormat = SHEXC,
      extractorTimeout = Some(1.micro))
      .assertThrows[StreamTimeoutException]
  }
}
