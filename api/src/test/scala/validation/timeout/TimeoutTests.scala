package org.ragna.comet
package validation.timeout

import data.DataFormat._
import exception.stream.timed.StreamTimeoutException
import schema.ShExSchemaFormat._
import utils.Samples.StreamSamples.mkSingleValidationResult

import cats.effect.testing.scalatest.AsyncIOSpec
import cats.implicits.catsSyntaxEitherId
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration._

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
      schemaFormat = SHEXC.asRight,
      extractorTimeout = Some(1.micro))
      .assertThrows[StreamTimeoutException]
  }
}
