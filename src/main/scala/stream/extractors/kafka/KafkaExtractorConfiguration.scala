package org.ragna.comet
package stream.extractors.kafka

import stream.extractors.StreamExtractor.Errors.mkExtractorCreationError
import stream.extractors.kafka.KafkaExtractorConfiguration.Constraints.*
import stream.extractors.kafka.KafkaExtractorConfiguration.Defaults.*
import stream.extractors.kafka.KafkaExtractorConfiguration.Errors.*
import utils.DurationValidationOps

import cats.implicits.catsSyntaxOptionId
import fs2.kafka.{AutoOffsetReset, CommitRecovery}

import scala.concurrent.duration.*

/**
 * Configuration and parameters related to how the program should interact with
 * a Kafka stream (stream origin location, committing, polling, etc.)
 *
 * Defaults are provided for most data, yet a target topic is required
 *
 * @param topic           Name of the topic from which data will be extracted
 * @param server          Hostname or IP address of the server broadcasting data
 * @param port            Port from which [[server]] is broadcasting
 * @param groupId         Id of the group to which the underlying Kafka consumer belongs 
 * @param offsetReset     Strategy to be followed when there are no available offsets
 *                        to consume on the consumer group
 * @param autoCommit      Whether to automatically commit offsets periodically
 *                        (see [[commitInterval]]) or not
 * @param sessionTimeout  Consumer timeout for connecting to Kafka to or processing
 *                        incoming items, some Kafka servers may limit require
 *                        certain session timeouts 
 * @param commitRecovery  Strategy to be followed when a commit operation fails
 * @param commitTimeout   Time to wait for offsets to be committed before raising
 *                        an error
 * @param commitInterval  Time to wait before performing an offset
 *                        commit operation
 * @param pollTimeout     How long the an stream polling operation is allowed to block 
 * @param pollInterval    Time interval between poll operations
 * @param concurrentItems Maximum number of items to be extracted and parsed 
 *                        for RDF in parallel
 *                        (set it to 1 for sequential execution, bear in mind that high values won't necessarily 
 *                        translate into performance improvements unless you 
 *                        know what you are doing)
 * @note Commit-behaviour settings are meaningless if [[autoCommit]] is set to false
 * @note Some restrictions are in place for configuration instances, and invalid
 *       values for the configuration data (e.g.: invalid port, instant timeouts/intervals)
 *       will not be accepted (see [[testConfiguration]])
 */
sealed case class KafkaExtractorConfiguration
(
  topic: String,
  server: String = defaultServerLocation,
  port: Int = defaultServerPort,
  groupId: String = defaultConsumerGroupId,
  offsetReset: AutoOffsetReset = defaultOffsetResetStrategy,
  autoCommit: Boolean = defaultAutoCommit,
  sessionTimeout: FiniteDuration = defaultSessionTimeout,
  commitRecovery: CommitRecovery = defaultCommitRecovery,
  commitTimeout: FiniteDuration = defaultCommitTimeout,
  commitInterval: FiniteDuration = defaultCommitInterval,
  pollTimeout: FiniteDuration = defaultPollTimeout,
  pollInterval: FiniteDuration = defaultPollInterval,
  concurrentItems: Int = defaultConcurrentEvaluations
) {

  /**
   * Kafka's target bootstrap servers, made by chaining the target host
   * and the target port
   */
  val bootstrapServers: String = s"$server:$port"

  /**
   * Perform several checks to make sure that this configuration:
   * - Contains valid inputs (e.g.: target port is in range)
   * - Contains reasonable inputs (e.g.: time intervals or timeouts durations make sense)
   *
   * @note Testing the configuration only available from the KafkaExtractor
   *       and related locations since it raises errors that need management
   * @throws IllegalArgumentException On invalid configuration parameters
   */
  private[kafka] def testConfiguration(): Unit = {
    // Check valid port
    require(isValidPort(port), invalidPort(port.some))
    // Check valid timings
    require(isValidDuration(pollTimeout), invalidPollTimeout(pollTimeout.some))
    require(isValidDuration(pollInterval), invalidPollInterval(pollInterval.some))
    require(isValidDuration(commitTimeout), invalidCommitTimeout(commitTimeout.some))
    require(isValidDuration(commitInterval), invalidCommitInterval(commitInterval.some))
  }
}

/**
 * Companion object with utils used in Kafka extractor configurations
 */
object KafkaExtractorConfiguration {

  /**
   * (Programmer-biased) constraints set upon the configuration values
   */
  private[kafka] object Constraints {
    /**
     * Minimum amount of time that is considered a reasonable amount
     *
     * Durations supplied to configurations should be higher than this
     */
    val minimumValidDuration: FiniteDuration = 10.millis

    /**
     * Maximum amount of time that is considered a reasonable amount
     *
     * Durations supplied to configurations should be lower than this
     */
    val maxValidDuration: FiniteDuration = 5.minutes


    /**
     * Given a certain number, confirm if it is a valid port number
     *
     * @see https://www.iana.org/assignments/service-names-port-numbers/service-names-port-numbers.xhtml
     */
    def isValidPort(input: Int): Boolean = input > 1 && input <= 65535

    /**
     * Given a certain duration, confirm it complies with this configuration's
     * constraints
     */
    def isValidDuration(duration: FiniteDuration): Boolean =
      duration.isBetween(minimumValidDuration, maxValidDuration)

    /**
     * Given a certain number, confirm if it is a valid number of items
     * to be processed concurrently
     *
     * @note We do no place any upper cap, but bear in mind that
     *       very high values won't translate to performance improvements
     */
    def isValidConcurrentValue(input: Int): Boolean = input > 0


  }

  /**
   * Default values used in the configuration as fallbacks when no
   * alternative has been received
   */
  private[kafka] object Defaults {

    /**
     * Default server from which to receive streaming data
     *
     * Attempt to listen on localhost
     */
    val defaultServerLocation: String = "localhost"

    /**
     * Default port from which to fetch streaming data in the server
     *
     * Uses Kafka's default port (9092)
     */
    val defaultServerPort: Int = 9092

    /**
     * Default consumer group ID assigned to the underlying Kafka consumer
     *
     * Uses the application's name and version to create a default ID
     */
    val defaultConsumerGroupId: String =
      s"${buildinfo.BuildInfo.name}:${buildinfo.BuildInfo.version}"

    /**
     * Default offset reset strategy
     *
     * Reset consumer to the latest available offset if none is available
     */
    val defaultOffsetResetStrategy: AutoOffsetReset = AutoOffsetReset.Latest

    /**
     * Default value for auto commit behaviour
     *
     * Defaults to auto committing
     */
    val defaultAutoCommit: Boolean = true

    /**
     * Default session timeout for connecting to Kafka
     *
     * Defaults to Java kafka client default
     */
    val defaultSessionTimeout: FiniteDuration = 10.seconds

    /**
     * Default commit recovery strategy
     *
     * Retry recoverable commit errors before giving up the stream
     */
    val defaultCommitRecovery: CommitRecovery = CommitRecovery.Default

    /**
     * Default wait for commits to be performed
     */
    val defaultCommitTimeout: FiniteDuration = 15.seconds

    /**
     * Default wait in between commits
     */
    val defaultCommitInterval: FiniteDuration = 15.seconds

    /**
     * Default wait for stream-polling to be performed
     */
    val defaultPollTimeout: FiniteDuration = 50.millis

    /**
     * Default wait in between stream-polls
     */
    val defaultPollInterval: FiniteDuration = 50.millis

    /**
     * Default amount of items that are evaluated concurrently
     */
    val defaultConcurrentEvaluations = 10

  }

  /**
   * Error messages and error-generating function to create descriptive
   * errors whenever a configuration is not valid
   */
  private[kafka] object Errors {
    /**
     * Generic function to create reasonable configuration errors
     *
     * @param `type`   Type of the error or name of the property that is not valid
     * @param badValue Optionally, the value that caused the error in the first place
     * @tparam A Type of [[badValue]]
     * @return String containing an error message informing of the error cause
     */
    private def mkError[A](`type`: String, badValue: Option[A]) = {
      val formattedValue = badValue.map(v => s" '$v' ").getOrElse(" ")
      mkExtractorCreationError(
        Some(s"Invalid value${formattedValue}supplied for ${`type`}")
      )
    }

    /**
     * @param timeout Invalid poll timeout supplied
     * @return An error message due to an invalid poll timeout having 
     *         being supplied
     */
    def invalidPollTimeout(timeout: Option[FiniteDuration]): String = mkError("poll timeout", timeout)

    /**
     * @param interval Invalid poll interval supplied
     * @return An error message due to an invalid poll interval having 
     *         being supplied
     */
    def invalidPollInterval(interval: Option[FiniteDuration]): String = mkError("poll interval", interval)

    /**
     * @param timeout Invalid commit timeout supplied
     * @return An error message due to an invalid commit timeout having 
     *         being supplied
     */
    def invalidCommitTimeout(timeout: Option[FiniteDuration]): String = mkError("commit timeout", timeout)

    /**
     * @param interval Invalid commit interval supplied
     * @return An error message due to an invalid commit interval having 
     *         being supplied
     */
    def invalidCommitInterval(interval: Option[FiniteDuration]): String = mkError("commit interval", interval)

    /**
     * @param port Invalid port supplied
     * @return An error message due to an invalid port having 
     *         being supplied
     */
    def invalidPort(port: Option[Int]): String = mkError("port", port)
  }
}