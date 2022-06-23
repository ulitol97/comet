package org.ragna.comet
package performace

import data.DataFormat
import data.DataFormat._
import exception.stream.timed.StreamTimeoutException
import implicits.RDFElementImplicits.rdfFromString
import stream.extractors.file.FileExtractor
import stream.extractors.kafka.{KafkaExtractor, KafkaExtractorConfiguration}
import stream.extractors.list.ListExtractor
import utils.{FileUtils, Samples}
import validation.Validator
import validation.configuration.ValidatorConfiguration
import validation.result.ResultStatus._
import validation.result.ValidationResult

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * Test suite for taking time measurements on different performace scenarios
 * of the app.
 *
 * The testing goes as follows:
 *
 * - Some trivial valid RDF/Schema combinations will be produced, 
 * complying with the apps testing framework (see [[Samples]])
 *
 * - This same data will be consumed by a validator, that will always be fed
 * through a [[ListExtractor]] for maximum efficiency and lowest interference.
 *
 * - Several amounts of RDF items will be testes against several levels
 * of parallelism.
 *
 * Tests are nested as follows to cover all possibilities:
 *
 * - Per amount of items
 *  - Per parallelization level
 */
//noinspection RedundantDefaultArgument
class PerformanceTests extends AsyncFreeSpec with AsyncIOSpec with Matchers {

  "1000 items" - {
    "1 thread" in {
      val validationResults =
        mkValidationResults(1000, 1)
      // We just need the test to succeed and see the elapsed time on the IDE
      validationResults.asserting(_ => true shouldBe true)
    }

    "2 threads" in {
      val validationResults =
        mkValidationResults(1000, 2)
      // We just need the test to succeed and see the elapsed time on the IDE
      validationResults.asserting(_ => true shouldBe true)
    }

    "4 threads" in {
      val validationResults =
        mkValidationResults(1000, 4)
      // We just need the test to succeed and see the elapsed time on the IDE
      validationResults.asserting(_ => true shouldBe true)
    }

    "6 threads" in {
      val validationResults =
        mkValidationResults(1000, 6)
      // We just need the test to succeed and see the elapsed time on the IDE
      validationResults.asserting(_ => true shouldBe true)
    }
  }

  "2000 items" - {
    "1 thread" in {
      val validationResults =
        mkValidationResults(2000, 1)
      // We just need the test to succeed and see the elapsed time on the IDE
      validationResults.asserting(_ => true shouldBe true)
    }

    "2 threads" in {
      val validationResults =
        mkValidationResults(2000, 2)
      // We just need the test to succeed and see the elapsed time on the IDE
      validationResults.asserting(_ => true shouldBe true)
    }

    "4 threads" in {
      val validationResults =
        mkValidationResults(2000, 4)
      // We just need the test to succeed and see the elapsed time on the IDE
      validationResults.asserting(_ => true shouldBe true)
    }

    "6 threads" in {
      val validationResults =
        mkValidationResults(2000, 6)
      // We just need the test to succeed and see the elapsed time on the IDE
      validationResults.asserting(_ => true shouldBe true)
    }
  }
  
  "4000 items" - {
    "1 thread" in {
      val validationResults =
        mkValidationResults(4000, 1)
      // We just need the test to succeed and see the elapsed time on the IDE
      validationResults.asserting(_ => true shouldBe true)
    }

    "2 threads" in {
      val validationResults =
        mkValidationResults(4000, 2)
      // We just need the test to succeed and see the elapsed time on the IDE
      validationResults.asserting(_ => true shouldBe true)
    }

    "4 threads" in {
      val validationResults =
        mkValidationResults(4000, 4)
      // We just need the test to succeed and see the elapsed time on the IDE
      validationResults.asserting(_ => true shouldBe true)
    }

    "6 threads" in {
      val validationResults =
        mkValidationResults(4000, 6)
      // We just need the test to succeed and see the elapsed time on the IDE
      validationResults.asserting(_ => true shouldBe true)
    }
  }

  "10000 items" - {
    "1 thread" in {
      val validationResults =
        mkValidationResults(10000, 1)
      // We just need the test to succeed and see the elapsed time on the IDE
      validationResults.asserting(_ => true shouldBe true)
    }

    "2 threads" in {
      val validationResults =
        mkValidationResults(10000, 2)
      // We just need the test to succeed and see the elapsed time on the IDE
      validationResults.asserting(_ => true shouldBe true)
    }

    "4 threads" in {
      val validationResults =
        mkValidationResults(10000, 4)
      // We just need the test to succeed and see the elapsed time on the IDE
      validationResults.asserting(_ => true shouldBe true)
    }

    "6 threads" in {
      val validationResults =
        mkValidationResults(10000, 6)
      // We just need the test to succeed and see the elapsed time on the IDE
      validationResults.asserting(_ => true shouldBe true)
    }
  }

  "30000 items" - {
    "1 thread" in {
      val validationResults =
        mkValidationResults(30000, 1)
      // We just need the test to succeed and see the elapsed time on the IDE
      validationResults.asserting(_ => true shouldBe true)
    }

    "2 threads" in {
      val validationResults =
        mkValidationResults(30000, 2)
      // We just need the test to succeed and see the elapsed time on the IDE
      validationResults.asserting(_ => true shouldBe true)
    }

    "4 threads" in {
      val validationResults =
        mkValidationResults(30000, 4)
      // We just need the test to succeed and see the elapsed time on the IDE
      validationResults.asserting(_ => true shouldBe true)
    }

    "6 threads" in {
      val validationResults =
        mkValidationResults(30000, 6)
      // We just need the test to succeed and see the elapsed time on the IDE
      validationResults.asserting(_ => true shouldBe true)
    }
  }

  /**
   * Return an [[IO]] containing the operation of making a validator
   * validate [[nItems]] RDF items using up to [[nThreads]] threads.
   * The validator will be fed through a [[ListExtractor]].
   *
   * @param nItems   Number of items to be fed to the validator
   * @param nThreads Number of threads allowed to be used by the extractor and
   *                 validator
   * @return [[IO]] containing the stream of validation results
   */
  private def mkValidationResults(nItems: Int, nThreads: Int): IO[List[ValidationResult]] =
    for {
      // Data, schema, trigger
      schema <- Samples.SchemaSamples.mkSchemaShExIO()
      trigger = Samples.TriggerSamples.mkTriggerShex()
      rdfItems = Samples.RdfSamples.mkRdfItems(nItems, TURTLE)

      // List extractor
      extractor = ListExtractor(rdfItems, DataFormat.TURTLE, concurrentItems = nThreads)

      // Validator init
      configuration = ValidatorConfiguration(schema, trigger, concurrentItems = nThreads)
      validator = new Validator(configuration, extractor)

      results <- validator.validate.compile.toList
    } yield results
}
