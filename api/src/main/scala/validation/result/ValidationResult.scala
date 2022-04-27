package org.ragna.comet
package validation.result

import validation.result.ResultStatus
import validation.result.ResultStatus.*
import validation.result.ValidationResult.*
import Messages.*

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.syntax.all.*
import es.weso.rdf.jena.RDFAsJenaModel
import es.weso.schema.Result as ValidationReport
import io.circe.Json

/**
 * Represents the output of validating a piece of streamed data
 *
 * @param inputResult Either the ShaclEx validation results or an error occurred
 *                    whilst computing them
 */
case class ValidationResult(private val inputResult: Either[Throwable, ValidationReport]) {

  /**
   * Final status of the validation
   */
  val status: ResultStatus =
    ResultStatus.fromValidationReport(inputResult.toOption)

  /**
   * Final result of the validation exposed to the API
   *
   * Will be empty for [[ERRORED]] validations and filled for 
   * [[VALID]] or [[INVALID]] validations
   */
  val result: Option[ValidationReport] = inputResult.toOption

  /**
   * High-level message accompanying the result
   *
   * @note For [[ERRORED]] results, one would assume that the input [[result]] will be
   *       a Left, but we use [[unknownErrorMessage]] as a fallback error message
   */
  val message: String = status match {
    case VALID => validMessage
    case INVALID => invalidMessage
    case ERRORED =>
      inputResult.leftMap(_.getMessage).left
        .getOrElse(unknownErrorMessage)
  }


  /**
   * @return Custom string representation of this validation result,
   *         including its status, companion message and the validation report
   *         (if available)
   */
  override def toString: String =
    s"""
       |Validation result:
       |- STATUS: $status
       |- MESSAGE: $message
       |- REPORT: ${result.map(getValidationReportJson).map(_.spaces2).getOrElse(noReportMessage)}
       |""".stripMargin.strip()
}

object ValidationResult {

  /**
   * From a ShaclEx validation report, create a message with its contents if
   * possible, based on its JSON representation
   *
   * @param report Report to be formatted to text
   * @return JSON containing the [[report]] information
   */
  private def getValidationReportJson(report: ValidationReport): Json =
    // Convert ValidationResult to JSON
    val validationResultJson: IO[Json] = for {
      emptyResource <- RDFAsJenaModel.empty
      json <- emptyResource.use(report.toJson(_))
    } yield json
    // Forced to unsafe run because of ShaclEx implementation
    validationResultJson.unsafeRunSync()

  /**
   * Utility messages used in the validation results
   */
  private[result] object Messages {
    /**
     * Message placed on a valid result
     */
    val validMessage = "Data validation was successful"

    /**
     * Message placed on a invalid result
     */
    val invalidMessage = "The data provided did not comply with the schema provided"

    /**
     * Placeholder messaged placed on a result whose error is unknown
     */
    val unknownErrorMessage = "An unknown error occurred validating this result"

    /**
     * Placeholder messaged placed on a result when it has no validation
     * report available yet it was required
     */
    val noReportMessage = "No validation report available"
  }
}
