package org.ragna.comet
package utils

import data.DataFormat
import data.DataFormat._
import exception.stream.timed.StreamTimeoutException
import implicits.RDFElementImplicits.rdfFromString
import schema.ShExSchemaFormat
import schema.ShExSchemaFormat.SHEXC
import stream.extractors.list.ListExtractor
import trigger.ShapeMapFormat.COMPACT
import trigger.{ShapeMapFormat, TriggerShapeMap, TriggerTargetDeclarations, ValidationTrigger}
import validation.Validator
import validation.configuration.ValidatorConfiguration
import validation.result.ValidationResult

import cats.effect.IO
import es.weso.schema.{Schema, Schemas}

import java.text.SimpleDateFormat
import java.util.{Date, Locale}
import scala.concurrent.duration.FiniteDuration
import scala.util.Random

/**
 * Object containing fixed data examples to be re-used in tests, as well as other
 * utility functions to dynamically generate datasets for testing
 *
 * Examples are provided for the most used and better supported data/schema formats:
 * - RDF and SHACL schemas: Turtle, JSON-LD, RDF/XML
 * - ShEx schemas: ShExC
 * - Validation triggers: 
 *    - ShEx => ShapeMaps: Compact
 *    - SHACL => Not needed
 *
 * Most of the testing gimmick is based on RDF data containing a temperature
 * and schemas validating if such temperatures are in a certain range
 *
 * @note Further support for other data/schema formats is needed,
 *       we are reliant on SHaclEX
 * @note Examples in RDFShape tool (for guidance):
 *       - Example ShEx: https://rdfshape.weso.es/link/16478801915
 *       - Example SHACL: https://rdfshape.weso.es/link/16490955842
 */
//noinspection HttpUrlsUsage
object Samples {

  /**
   * Minimum accepted temperature in the temperature range used in the test examples
   * This value is used to create restrictions when generating schemas
   */
  private val minValidTemperature: Double = 18.00

  /**
   * Maximum accepted temperature in the temperature range used in the test examples
   * This value is used to create restrictions when generating schemas
   */
  private val maxValidTemperature: Double = 20.00

  /**
   * Utils to create validation streams for testing
   */
  object StreamSamples {
    /**
     * Similar to the alternative implementation of [[mkSingleValidationResult]],
     * but here the RDF string is not passed manually but created from a template
     * given its desired format
     *
     * @param rdfFormat        Format of the RDF item that we want to get validated,
     *                         generated from template (see [[RdfSamples]])
     * @param schemaFormat     Format of the Schema to be used for validation,
     *                         generated from template (see [[SchemaSamples]])
     * @param valid            Whether if the produced RDF item should comply with
     *                         the produced validation schema or not
     *                         Used to control de output status of the validation
     * @param haltOnInvalid    Whether if the underlying validator should halt on
     *                         INVALID validation results
     * @param haltOnError      Whether if the underlying validator should halt on
     *                         ERRORED validation results
     * @param extractorTimeout Time for the item extractor to wait without 
     *                         receiving items before erroring and closing
     *                         the stream (see [[StreamTimeoutException]])
     * @return The [[ValidationResult]] of getting an RDF item (of format [[rdfFormat]])
     *         through a validator using a Schema (of format/engine [[schemaFormat]])
     *         following the data, schema and trigger templates in [[Samples]]
     */
    def mkSingleValidationResult(rdfFormat: DataFormat,
                                 schemaFormat: Either[DataFormat, ShExSchemaFormat],
                                 valid: Boolean = true,
                                 haltOnInvalid: Boolean = false,
                                 haltOnError: Boolean = false,
                                 extractorTimeout: Option[FiniteDuration] = None
                                ): IO[ValidationResult] = {
      // Make the RDF item
      val rdfItem = RdfSamples.mkRdfItem(rdfFormat, valid = valid)
      // Resort to alternative implementation
      mkSingleValidationResult(
        rdfItem, rdfFormat, schemaFormat, haltOnInvalid, haltOnError, extractorTimeout
      )
    }

    /**
     * Shortcut for generating single-item results through comet's stream validators,
     * which is the main logic required for these tests
     *
     * Given the details of the validation (input data/schema format, wanted result...)
     * this creates a validation stream that complies with it and gets its eventual
     * result back
     *
     * @param rdfItem          Custom string of RDF data provided for validation
     * @param rdfFormat        Format of the RDF string provided for validation
     * @param schemaFormat     Format of the Schema to be used for validation,
     *                         generated from template (see [[SchemaSamples]])
     * @param haltOnInvalid    Whether if the underlying validator should halt on
     *                         INVALID validation results
     * @param haltOnError      Whether if the underlying validator should halt on
     *                         ERRORED validation results
     * @param extractorTimeout Time for the item extractor to wait without 
     *                         receiving items before erroring and closing
     *                         the stream (see [[StreamTimeoutException]])
     * @return The [[ValidationResult]] of getting a custom RDF string, expected
     *         to have format [[rdfFormat]],
     *         through a validator using a Schema (of format/engine [[schemaFormat]])
     *         following the schema and trigger templates in [[Samples]]
     *
     * @note The List extractor is used for testing, since it is the simplest
     *       and cheapest way we have to test in-memory data
     */
    def mkSingleValidationResult(rdfItem: String,
                                 rdfFormat: DataFormat,
                                 schemaFormat: Either[DataFormat, ShExSchemaFormat],
                                 haltOnInvalid: Boolean,
                                 haltOnError: Boolean,
                                 extractorTimeout: Option[FiniteDuration]
                                ): IO[ValidationResult] = {
      for {
        // Make the schema
        schema <- schemaFormat match {
          case Left(dataFormat) => SchemaSamples.mkSchemaShaclIO(dataFormat)
          case Right(schemaFormat) => SchemaSamples.mkSchemaShExIO(schemaFormat)
        }
        // Make the validation trigger (inferred from schema type)
        trigger = schemaFormat match {
          case Left(_) => TriggerSamples.mkTriggerShacl
          case Right(_) => TriggerSamples.mkTriggerShex(COMPACT)
        }
        // Get the RDF item into a list extractor
        extractor: ListExtractor[String] = ListExtractor(items = List(rdfItem), format = rdfFormat,
          itemTimeout = extractorTimeout)

        // Open validation stream and collect the validation results
        // Validator settings
        validatorConfiguration = ValidatorConfiguration(schema, trigger,
          haltOnInvalid = haltOnInvalid, haltOnErrored = haltOnError)
        validator = new Validator(validatorConfiguration, extractor)
        results <- validator.validate.compile.toList
      } yield results.head
    }

  }

  /**
   * Utils to create RDF data Strings for testing
   */
  object RdfSamples {

    /**
     * Date formatter used to include dates in the fabricated data
     */
    // noinspection SpellCheckingInspection
    private val dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

    /**
     * Generate a dataset of RDF strings
     *
     * @param size   Amount of items to be generated
     * @param format Format in which the RDF items are redacted
     * @param min    Minimum temperature of the items
     * @param max    Maximum temperature of the items
     * @return A list of Strings, each one being an RDF text with a single
     *         sensor temperature reading
     */
    def mkRdfItems(size: Int, format: DataFormat = TURTLE,
                   min: Double = minValidTemperature,
                   max: Double = maxValidTemperature): List[String] =
      (0 until size).map(_ => mkRdfItem(format, min, max)).toList


    /**
     * Logic for creating RDF strings from a template, which is
     * selected depending on the format and injected with the current date and
     * a temperature in the range specified by the user
     *
     * @param format Format of the output RDF string
     * @param min    Minimum temperature of the item
     * @param max    Maximum temperature of the items
     * @param valid  Whether the produced item should comply with the schemas
     *               generated for testing or not
     * @return A String containing RDF data with the format and contents 
     *         specified by the user
     *
     * @note If no parameters are given, the resulting item will be a
     *       TURTLE item complying with the temperature restrictions used by
     *       test schemas
     */
    def mkRdfItem(format: DataFormat = TURTLE,
                  min: Double = minValidTemperature,
                  max: Double = maxValidTemperature,
                  valid: Boolean = true): String = {
      val dateFormatted = dateFormatter.format(new Date())
      // Force a too low temperature to make items invalid if necessary
      val temperature = if (valid) Random.between(min, max) else minValidTemperature - 1
      // Format with US locale to have dots, not commas
      val temperatureFormatted = String.format(Locale.US, "%.2f", temperature)

      // Return the RDF template injected with data, choosing from the given format
      format match {
        case RDFXML =>
          f"""
             |<rdf:RDF
             |    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
             |    xmlns:ex="http://example.org/"
             |    xmlns:xsd="http://www.w3.org/2001/XMLSchema#">
             |  <ex:sensorReading rdf:about="http://example.org/reading">
             |    <ex:readingDateTime rdf:datatype="http://www.w3.org/2001/XMLSchema#dateTime"
             |    >$dateFormatted</ex:readingDateTime>
             |    <ex:readingTemperature rdf:datatype="http://www.w3.org/2001/XMLSchema#decimal"
             |    >$temperatureFormatted</ex:readingTemperature>
             |    <ex:status>OK</ex:status>
             |  </ex:sensorReading>
             |</rdf:RDF>
             |""".stripMargin.strip

        case JSONLD =>
          f"""
             |{
             |  "@id" : "ex:reading",
             |  "@type" : "ex:sensorReading",
             |  "readingDateTime" : "$dateFormatted",
             |  "readingTemperature" : "$temperatureFormatted",
             |  "status" : "OK",
             |  "@context" : {
             |    "readingDateTime" : {
             |      "@id" : "http://example.org/readingDateTime",
             |      "@type" : "http://www.w3.org/2001/XMLSchema#dateTime"
             |    },
             |    "readingTemperature" : {
             |      "@id" : "http://example.org/readingTemperature",
             |      "@type" : "http://www.w3.org/2001/XMLSchema#decimal"
             |    },
             |    "status" : {
             |      "@id" : "http://example.org/status"
             |    },
             |    "ex" : "http://example.org/",
             |    "xsd" : "http://www.w3.org/2001/XMLSchema#"
             |  }
             |}
             |""".stripMargin.strip


        // Default to Turtle
        case TURTLE | _ =>
          f"""
             |@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
             |@prefix ex:      <http://example.org/> .
             |
             |ex:reading a ex:sensorReading ;
             |          ex:readingDateTime "$dateFormatted"^^xsd:dateTime ;
             |          ex:readingTemperature "$temperatureFormatted"^^xsd:decimal ;
             |		  ex:status "OK" .
             |""".stripMargin.strip
      }

    }
  }

  /**
   * Utils to create SHaclEX schemas for testing
   */
  object SchemaSamples {
    /**
     * Logic for creating ShEx schemas from a template, which is
     * selected depending on the format and injected with the valid temperature
     * limits
     *
     * @param format Format of the output ShEx schema
     * @return A SHaclEX [[Schema]] restricting its input data to have a valid
     *         timestamp and one or more valid temperature readings
     *
     * @note If no parameters are given, the resulting item will be a
     *       SHEX schema using SHEXC syntax
     */
    def mkSchemaShExIO(format: ShExSchemaFormat = SHEXC): IO[Schema] = {
      val schemaText = format match {
        case SHEXC | _ =>
          f"""|PREFIX ex:       <http://example.org/>
              |PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>
              |
              |# Filters of a valid sensor reading
              |ex:ValidReading {
              |  ex:readingDateTime     xsd:dateTime  ; # Has a VALID timestamp
              |  ex:readingTemperature xsd:decimal MININCLUSIVE $minValidTemperature MAXINCLUSIVE $maxValidTemperature + ; # 1+ readings in range 18-20
              |  ex:status [ "OK" "RUNNING" ] # Status must be one of 
              |}
              |""".stripMargin.strip

        // Better support needed for ShExJ
        /*
        case SHEXJ =>
          f"""
             |{
             |  "type" : "Schema",
             |  "@context" : "http://www.w3.org/ns/shex.jsonld",
             |  "shapes" : [
             |    {
             |      "type" : "Shape",
             |      "id" : "http://example.org/ValidReading",
             |      "expression" : {
             |        "type" : "EachOf",
             |        "expressions" : [
             |          {
             |            "type" : "TripleConstraint",
             |            "predicate" : "http://example.org/readingDateTime",
             |            "valueExpr" : {
             |              "type" : "NodeConstraint",
             |              "datatype" : "http://www.w3.org/2001/XMLSchema#dateTime"
             |            }
             |          },
             |          {
             |            "predicate" : "http://example.org/readingTemperature",
             |            "valueExpr" : {
             |              "type" : "NodeConstraint",
             |              "datatype" : "http://www.w3.org/2001/XMLSchema#decimal",
             |              "mininclusive" : $minValidTemperature,
             |              "maxinclusive" : $maxValidTemperature
             |            },
             |            "min" : 1,
             |            "max" : -1,
             |            "type" : "TripleConstraint"
             |          },
             |          {
             |            "type" : "TripleConstraint",
             |            "predicate" : "http://example.org/status",
             |            "valueExpr" : {
             |              "type" : "NodeConstraint",
             |              "values" : [
             |                {
             |                  "value" : "OK"
             |                },
             |                {
             |                  "value" : "RUNNING"
             |                }
             |              ]
             |            }
             |          }
             |        ]
             |      }
             |    }
             |  ]
             |}
             |""".stripMargin 
             */
      }

      Schemas.fromString(schemaText, format.name, Schemas.shEx.name)
    }

    /**
     * Logic for creating SHACL schemas from a template, which is
     * selected depending on the format and injected with the valid temperature
     * limits
     *
     * @param format Format of the output SHACL schema
     * @return A SHaclEX [[Schema]] restricting its input data to have a valid
     *         timestamp and one or more valid temperature readings
     *
     * @note If no parameters are given, the resulting item will be a
     *       SHACL schema using TURTLE syntax
     */
    def mkSchemaShaclIO(format: DataFormat = TURTLE): IO[Schema] = {
      val schemaText = format match {
        case RDFXML =>
          f"""
             |<rdf:RDF
             |    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
             |    xmlns:ex="http://example.org/"
             |    xmlns:sh="http://www.w3.org/ns/shacl#"
             |    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
             |    xmlns:xsd="http://www.w3.org/2001/XMLSchema#">
             |  <sh:NodeShape rdf:about="http://example.org/ValidReading">
             |    <sh:targetClass rdf:resource="http://example.org/sensorReading"/>
             |    <sh:property>
             |      <sh:PropertyShape>
             |        <sh:path rdf:resource="http://example.org/status"/>
             |        <sh:pattern>OK|RUNNING</sh:pattern>
             |        <sh:maxCount rdf:datatype="http://www.w3.org/2001/XMLSchema#integer"
             |        >1</sh:maxCount>
             |        <sh:datatype rdf:resource="http://www.w3.org/2001/XMLSchema#string"/>
             |      </sh:PropertyShape>
             |    </sh:property>
             |    <sh:property>
             |      <sh:PropertyShape>
             |        <sh:path rdf:resource="http://example.org/readingDateTime"/>
             |        <sh:datatype rdf:resource="http://www.w3.org/2001/XMLSchema#dateTime"/>
             |      </sh:PropertyShape>
             |    </sh:property>
             |    <sh:property>
             |      <sh:PropertyShape>
             |        <sh:path rdf:resource="http://example.org/readingTemperature"/>
             |        <sh:maxInclusive rdf:datatype="http://www.w3.org/2001/XMLSchema#decimal"
             |        >$maxValidTemperature</sh:maxInclusive>
             |        <sh:minInclusive rdf:datatype="http://www.w3.org/2001/XMLSchema#decimal"
             |        >$minValidTemperature</sh:minInclusive>
             |        <sh:minCount rdf:datatype="http://www.w3.org/2001/XMLSchema#integer"
             |        >1</sh:minCount>
             |        <sh:datatype rdf:resource="http://www.w3.org/2001/XMLSchema#decimal"/>
             |      </sh:PropertyShape>
             |    </sh:property>
             |  </sh:NodeShape>
             |</rdf:RDF>
             |""".stripMargin.strip

        case JSONLD =>
          f"""
             |{
             |  "@graph" : [ {
             |    "@id" : "_:b0",
             |    "@type" : "sh:PropertyShape",
             |    "datatype" : "xsd:decimal",
             |    "maxInclusive" : "$maxValidTemperature",
             |    "sh:minCount" : 1,
             |    "minInclusive" : "$minValidTemperature",
             |    "path" : "ex:readingTemperature"
             |  }, {
             |    "@id" : "_:b1",
             |    "@type" : "sh:PropertyShape",
             |    "datatype" : "xsd:string",
             |    "sh:maxCount" : 1,
             |    "path" : "ex:status",
             |    "pattern" : "OK|RUNNING"
             |  }, {
             |    "@id" : "_:b2",
             |    "@type" : "sh:PropertyShape",
             |    "datatype" : "xsd:dateTime",
             |    "path" : "ex:readingDateTime"
             |  }, {
             |    "@id" : "ex:ValidReading",
             |    "@type" : "sh:NodeShape",
             |    "property" : [ "_:b1", "_:b2", "_:b0" ],
             |    "targetClass" : "ex:sensorReading"
             |  } ],
             |  "@context" : {
             |    "path" : {
             |      "@id" : "http://www.w3.org/ns/shacl#path",
             |      "@type" : "@id"
             |    },
             |    "maxInclusive" : {
             |      "@id" : "http://www.w3.org/ns/shacl#maxInclusive",
             |      "@type" : "http://www.w3.org/2001/XMLSchema#decimal"
             |    },
             |    "minInclusive" : {
             |      "@id" : "http://www.w3.org/ns/shacl#minInclusive",
             |      "@type" : "http://www.w3.org/2001/XMLSchema#decimal"
             |    },
             |    "minCount" : {
             |      "@id" : "http://www.w3.org/ns/shacl#minCount",
             |      "@type" : "http://www.w3.org/2001/XMLSchema#integer"
             |    },
             |    "datatype" : {
             |      "@id" : "http://www.w3.org/ns/shacl#datatype",
             |      "@type" : "@id"
             |    },
             |    "targetClass" : {
             |      "@id" : "http://www.w3.org/ns/shacl#targetClass",
             |      "@type" : "@id"
             |    },
             |    "property" : {
             |      "@id" : "http://www.w3.org/ns/shacl#property",
             |      "@type" : "@id"
             |    },
             |    "pattern" : {
             |      "@id" : "http://www.w3.org/ns/shacl#pattern"
             |    },
             |    "maxCount" : {
             |      "@id" : "http://www.w3.org/ns/shacl#maxCount",
             |      "@type" : "http://www.w3.org/2001/XMLSchema#integer"
             |    },
             |    "ex" : "http://example.org/",
             |    "sh" : "http://www.w3.org/ns/shacl#",
             |    "rdf" : "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
             |    "xsd" : "http://www.w3.org/2001/XMLSchema#",
             |    "rdfs" : "http://www.w3.org/2000/01/rdf-schema#"
             |  }
             |}
             |""".stripMargin.strip

        // Default to Turtle
        case TURTLE | _ =>
          f"""
             |@prefix sh: <http://www.w3.org/ns/shacl#> .
             |@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
             |@prefix ex:      <http://example.org/> .
             |
             |# Filters of a valid sensor reading
             |ex:ValidReading a sh:NodeShape ;
             |                sh:targetClass  ex:sensorReading ;
             |                sh:property [
             |    				sh:path     ex:readingDateTime ;
             |                    sh:datatype xsd:dateTime ;
             |  				] ;
             |      			sh:property [
             |    				sh:path     ex:readingTemperature ;
             |                    sh:datatype xsd:decimal ;
             |                    sh:minInclusive $minValidTemperature;
             |                    sh:maxInclusive $maxValidTemperature ;
             |                    sh:minCount 1 ; # 1+ readings
             |  				] ;
             |      			sh:property [
             |    				sh:path     ex:status ;
             |                    sh:datatype xsd:string ;
             |                    sh:pattern "OK|RUNNING" ; # Range of states
             |                    sh:maxCount 1 ; # 1 single status
             |  				] .
             |""".stripMargin.strip
      }
      Schemas.fromString(schemaText, format.name, Schemas.shaclex.name)
    }
  }

  /**
   * Utils to create SHaclEX validation triggers schemas for testing
   */
  object TriggerSamples {

    /**
     * Create a validation trigger for SHACL validations
     *
     * @return The de-facto trigger for SHACL
     */
    def mkTriggerShacl: ValidationTrigger =
      TriggerTargetDeclarations()

    /**
     * Create a validation trigger for ShEx validations, creating the contained
     * ShapeMaps from a template selected upon the ShapeMap desired format
     *
     * @param format Format of the ShapeMap contained in the validation trigger
     * @return A SHaclEX validation trigger pre-configured to validate the
     *         data generated for testing against the shapes defined in the schemas
     *         generated for testing
     */
    def mkTriggerShex(format: ShapeMapFormat = COMPACT): ValidationTrigger = {
      val shapeMapText = format match {
        // Default to compact
        case COMPACT | _ => """ex:reading@ex:ValidReading""".stripMargin.strip
        // Further support needed for JSON
        /*case JSON =>
          """
            |[
            |  { "node": "<ex:reading>",
            |    "shape": "<ex:ValidReading>" }
            |]
            |""".stripMargin.strip
        */
      }
      TriggerShapeMap(shapeMapText, format)
    }
  }

}
