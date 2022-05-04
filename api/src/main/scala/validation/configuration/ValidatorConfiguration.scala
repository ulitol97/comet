package org.ragna.comet
package validation.configuration


import trigger.ValidationTrigger
import validation.configuration.ValidatorConfiguration.Defaults._
import validation.configuration.ValidatorConfiguration.Errors

import es.weso.schema.Schema

/**
 * Contains the data required for a [[Validator]] to perform an RDF validation
 * against a given Schema and how to behave regarding the validation results
 *
 * @param schema          Schema against which the validation will be performed for
 *                        all items processed by this validator
 * @param trigger         ValidationTrigger against which the validation will be performed for
 *                        all items processed by this validator
 * @param haltOnInvalid   Whether the validator should stop and raise an
 *                        [[StreamInvalidItemException]]
 *                        on an invalid item or emit an [[INVALID]] result
 *                        and keep processing items
 *
 *                        If this is set to true, remember to handle [[StreamInvalidItemException]]s
 *                        on the validator's output stream
 * @param haltOnErrored   Whether the validator should stop and raise an
 *                        [[StreamErroredItemException]]
 *                        when an unexpected error occurs or just emit
 *                        an [[ERRORED]] result containing the error.
 *
 *                        If this is set to true, remember to handle [[StreamErroredItemException]]s
 *                        on the validator's output stream                        
 * @param concurrentItems Maximum number of RDF items to be validated in parallel
 *                        (set it to 1 for sequential execution, bear in mind
 *                        that high values won't necessarily translate into
 *                        performance improvements unless you know what you 
 *                        are doing)
 */
sealed case class ValidatorConfiguration(
                                          schema: Schema,
                                          trigger: ValidationTrigger,
                                          haltOnInvalid: Boolean = defaultHaltOnInvalid,
                                          haltOnErrored: Boolean = defaultHaltOnErrored,
                                          concurrentItems: Int = defaultConcurrentValidations
                                        ) {
  // Ensure user inputs are valid
  checkConfiguration()

  /**
   * Check the user-controlled inputs to this configuration, preventing
   * the creation of it if necessary
   *
   * @throws IllegalArgumentException On invalid extractor parameters
   */
  private def checkConfiguration(): Unit = {
    require(concurrentItems > 0, Errors.invalidConcurrentItems)
  }
}

object ValidatorConfiguration {
  /**
   * Default values used as fallbacks when no
   * alternative has been received
   */
  object Defaults {

    /**
     * Default behaviour regarding stopping the validation on invalid items
     */
    val defaultHaltOnInvalid = false

    /**
     * Default behaviour regarding stopping the validation on erroring items
     */
    val defaultHaltOnErrored = false

    /**
     * Default amount of items that can be validated concurrently
     */
    val defaultConcurrentValidations = 10
  }

  /**
   * Error messages for validator configurations
   */
  private[configuration] object Errors {

    /**
     * Message thrown when the input concurrency is invalid
     */
    val invalidConcurrentItems: String =
      mkConfigurationCreationError(Some("Concurrent items must be > 0"))

    /**
     * Text preceding any error message raised during the creation of the
     * configuration
     */
    private val configurationCreationErrorPrefix: String =
      "Could not create validator configuration from the supplied data"

    /**
     * Method for creating descriptive error messages for errors that arose 
     * during the creation of the configuration
     *
     * @param message Description of the error
     * @return String containing the specific error ([[message]])
     *         preceded by [[configurationCreationErrorPrefix]]
     */
    //noinspection DuplicatedCode
    def mkConfigurationCreationError(message: Option[String]): String = {
      val attachedMessage = message.map(msg => s": $msg").getOrElse("")
      s"$configurationCreationErrorPrefix$attachedMessage"
    }

  }
}
