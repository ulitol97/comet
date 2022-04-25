package org.ragna.comet

import data.DataFormat
import exception.stream.validations.*
import implicits.RDFElementImplicits.rdfFromString
import model.rdf.RDFElement
import stream.StreamSource
import stream.extractors.StreamExtractor
import stream.extractors.file.{Charsets, FileExtractor}
import stream.extractors.kafka.{KafkaExtractor, KafkaExtractorConfiguration}
import stream.extractors.list.ListExtractor
import trigger.ShapeMapFormat.COMPACT
import trigger.{ShapeMapFormat, TriggerShapeMap, TriggerTargetDeclarations, ValidationTrigger}
import utils.StreamUtils.*
import utils.{FileUtils, IODebugOps, StreamUtils, Timer}
import validation.Validator
import validation.configuration.ValidatorConfiguration

import cats.effect.*
import cats.syntax.functor.*
import es.weso.rdf.PrefixMap
import es.weso.rdf.jena.RDFAsJenaModel
import es.weso.schema.{Schema, Schemas, ShapeMapTrigger, ValidationTrigger as ValidationTriggerShaclex}
import fs2.io.file.*
import fs2.{Pipe, Stream}

import java.text.SimpleDateFormat
import java.util.{Calendar, Date, Locale}
import scala.concurrent.duration.*
import scala.util.Random

/**
 * Demo entry point.
 * Shall initialize the validation, get the validation Stream
 * and print it
 *
 */
object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    // Logic to create the files-resource in case the test FileExtractor
    val filesResource = FileUtils.createFiles(TestData.rdfItems)

    // All program logic, wrapped in the files resource so we can test files
    val program =
      filesResource.use(
        filePaths =>
          for {
            // Schema for validations
            schema <- TestData.mkSchemaShaclIO()
            // Trigger for validations
            trigger = TestData.mkTriggerShacl
            // Validator settings
            validatorConfiguration = ValidatorConfiguration(schema, trigger, haltOnInvalid = true, haltOnErrored = true)
            // RDF extractors: all types ready to be tested
            // - List extractor
            listExtractor = ListExtractor(
              items = TestData.rdfItems,
              format = DataFormat.TURTLE,
              itemTimeout = None)

            // - Kafka extractor
            //            kafkaExtractorConfiguration = KafkaExtractorConfiguration("rdf", "localhost", 9092)
            kafkaExtractorConfiguration = KafkaExtractorConfiguration(
              topic = "rdf",
              server = "localhost",
              port = 9092)
            kafkaExtractor = KafkaExtractor[Unit, String](
              configuration = kafkaExtractorConfiguration,
              format = DataFormat.TURTLE,
              itemTimeout = Some(5.seconds))

            // - Files Extractor
            fileExtractor = FileExtractor(
              files = filePaths,
              charset = Charsets.UTF8.value,
              format = DataFormat.TURTLE,
              itemTimeout = None)

            // Validator instance
            validator = new Validator(validatorConfiguration, listExtractor)
            // Open validation stream
            app <- validator.validate // Init
              //              .delayBy(10.minute)
              //              .repeat
              .evalTap { _ => IO.println("- Received item") }
              //              .through(toConsole) // Print validation results (see overridden toString)
              .handleErrorWith { err =>
                Stream.eval(IO.println(
                  s"KABOOM (${err.getClass.getSimpleName}): " + err.getMessage))
              }
              .onFinalize(IO.println("Main finalized"))
              .compile.drain

          } yield app)


    // Wrap app in timer
    val app = for {
      initTime <- IO(System.currentTimeMillis())
      _ <- program
      endTime <- IO((System.currentTimeMillis() - initTime) / 1000f)
      closing <- IO.println(s"Time: ${endTime}s")
    } yield ()
    // Execute app
    app >> IO.pure(ExitCode.Success)
  }
}

//noinspection HttpUrlsUsage
// Example ShEx: https://rdfshape.weso.es/link/16478801915
// Example SHACL: https://rdfshape.weso.es/link/16490955842
private object TestData {

  lazy val rdfItems: List[String] = Utils.mkRdfItems(testItems)

  // RDF items for testing
  private val testItems = 1


  // TESTING WITH A LIST OF FILES:
  //  1. Start a Stream with RDF texts
  //  2. EvalMap the Stream, use Files[IO] to write any incoming item to a file
  //  3. Finish the stream, store the list of files created.
  //  4. Options:
  //    1: - Create a custom resource (Resource.make) with the list of files
  //         Use Files[IO] to remove all files used after resource usage
  //       - Try Stream.resource method
  //    2: - Just store the list of files and create a function capable of removing files
  //       - Use Stream.bracket, passing it the list of files and the function to remove them

  // ShEx Schema used for testing
  private val schemaShexStr: String =
    """|PREFIX ex:       <http://example.org/>
       |PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>
       |
       |# Filters of a valid sensor reading
       |ex:ValidReading {
       |  ex:readingDatetime     xsd:dateTime  ; # Has a VALID timestamp
       |  ex:readingTemperature xsd:decimal MININCLUSIVE 18 MAXINCLUSIVE 20 + ; # 1+ readings in range 18-20
       |  ex:status [ "OK" "RUNNING" ] # Status must be one of 
       |}
       |""".stripMargin.strip

  // SHACL Schema used for testing
  private val schemaShaclStr: String =
    """|@prefix sh: <http://www.w3.org/ns/shacl#> .
       |@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
       |@prefix ex:      <http://example.org/> .
       |
       |# Filters of a valid sensor reading
       |ex:ValidReading a sh:NodeShape ;
       |                sh:targetClass  ex:sensorReading ;
       |                sh:property [
       |    				sh:path     ex:readingDatetime ;
       |                    sh:datatype xsd:dateTime ;
       |  				] ;
       |      			sh:property [
       |    				sh:path     ex:readingTemperature ;
       |                    sh:datatype xsd:decimal ;
       |                    sh:minInclusive 18;
       |                    sh:maxInclusive 20 ;
       |                    sh:minCount 1 ; # 1+ readings
       |  				] ;
       |      			sh:property [
       |    				sh:path     ex:status ;
       |                    sh:datatype xsd:string ;
       |                    sh:pattern "OK|RUNNING" ; # Range of states
       |                    sh:maxCount 1 ; # 1 single status
       |  				] .
       |""".stripMargin.strip

  // Validation trigger (ShapeMap) for testing
  private val shapeMapStr = """ex:reading@ex:ValidReading""".stripMargin

  def mkSchemaShexIO(schemaText: String = schemaShexStr, format: String = "ShExC"): IO[Schema] =
    Schemas.fromString(schemaText, format, Schemas.shEx.name)

  def mkSchemaShaclIO(schemaText: String = schemaShaclStr): IO[Schema] =
    Schemas.fromString(schemaText, "Turtle", Schemas.shaclex.name)

  def mkTriggerShex(shapeMapText: String = shapeMapStr, format: ShapeMapFormat = COMPACT): ValidationTrigger =
    TriggerShapeMap(shapeMapText, format)


  // Validation trigger (target declarations) for testing
  def mkTriggerShacl: ValidationTrigger =
    TriggerTargetDeclarations()
}

//noinspection HttpUrlsUsage
private object Utils {

  //noinspection SpellCheckingInspection
  private val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

  // Make a list of RDF strings
  def mkRdfItems(size: Int, min: Double = 16, max: Double = 22.5): List[String] =
    (0 until size).map(_ => mkRdfItem(min, max)).toList

  // RDF Strings used for testing
  private def mkRdfItem(min: Double, max: Double): String = {
    val temperature = Random.between(min, max)
    mkRdfItem(temperature)
  }

  private def mkRdfItem(temperature: Double): String = {
    val dateFormatted = dateFormatter.format(new Date())
    // Format with US locale to have dots, not commas
    val temperatureFormatted = String.format(Locale.US, "%.2f", temperature)

    f"""
       |@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
       |@prefix ex:      <http://example.org/> .
       |
       |ex:reading a ex:sensorReading ;
       |          ex:readingDatetime "$dateFormatted"^^xsd:dateTime ;
       |          ex:readingTemperature "$temperatureFormatted"^^xsd:decimal ;
       |		  ex:status "OK" .
       |""".stripMargin.strip

  }
}
