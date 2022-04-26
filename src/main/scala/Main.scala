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
import utils.{IODebugOps, StreamUtils, Timer}
import validation.Validator
import validation.configuration.ValidatorConfiguration

import cats.effect.*
import cats.syntax.functor.*
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

  override def run(args: List[String]): IO[ExitCode] =
    IO.pure(ExitCode.Success)
}
