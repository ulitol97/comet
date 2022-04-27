package org.ragna.comet
package utils

import stream.extractors.file.{Charsets, FileExtractor}

import cats.effect.{IO, Resource}
import com.typesafe.scalalogging.LazyLogging
import fs2.io.file.*
import fs2.{Stream, text}

import java.nio.charset.Charset
import scala.concurrent.duration.*

/**
 * Utilities to work with files, in the context of FS2 Streams and using
 * FS2 files API
 *
 * Used for testing the [[FileExtractor]]
 */
object FileUtils extends LazyLogging {

  /**
   * From a list of texts, write each of them into a separate temporary file
   * and return a resource for using such files before having them disposed
   *
   * @param contents List of contents (strings) to be written into files
   * @param charset  Charset used in the data written
   * @return A cats Resource for using the created files as needed and making
   *         sure that all created files are removed after usage
   *
   * @note Publicly exposed, to make sure people using the API manages their
   *       files as resources
   */
  def createFiles(
                   contents: List[String],
                   charset: Charset = Charsets.UTF8.value
                 ): Resource[IO, List[Path]] = {
    Resource.make {
      // Acquire list of files
      IO {
        logger.info(s"Creating ${contents.length} temporary files")
      } >> createFilesFromContents(contents, charset)
    } { (filePaths: List[Path]) =>
      // Release list of files: for each file remove it and print a message
      // Compose an IO in charge of removing all files, which is the final one
      filePaths.foldLeft(IO.unit) { (curr, file) =>
        curr >> (Files[IO].deleteIfExists(file) >> IO {
          logger.debug(s"Removed file $file")
        })
      } >> IO {
        logger.info("Removed all files in resource")
      }
    }
  }

  /**
   * From a list of texts, write each of them into a separate temporary file
   * and return the resulting list of files
   *
   * @param contents List of contents (strings) to be written into files
   * @param charset  Charset used in the data written
   * @return The list of files created in the process (in the form of 
   *         [[Path]] instances), so that they can be further processed
   *
   * @note For internal use by other public utilities
   */
  private def createFilesFromContents(
                                       contents: List[String],
                                       charset: Charset
                                     ): IO[List[Path]] = {
    // Create Stream of Strings from list
    Stream.evalSeq(IO.pure(contents))
      // Create the temporary files and return each String along with its
      // corresponding File where it'll be written. String => (String, Path)
      .evalMap(contentStr => for {
        // For each string, create a temporary file and keep its reference
        newFile <- Files[IO].createTempFile
      } yield (contentStr, newFile)
      )
      // For each pair (String, Path), write each text to its file,
      // being returned the file path if successful. (String, Path) => Path
      .evalMap { case (contentStr, filePath) =>
        writeToFile(contentStr, filePath, charset)
      }
      // Compile and return the list of Paths
      .compile.toList
  }

  /**
   * Given some text and a target file, attempt to write to the file using
   * FS2 files API
   *
   * The file is always created if it does not exist
   *
   * @param content Text to be written into the file
   * @param file    Path of the file to be written to
   * @param charset Charset used in the data written
   * @param append  Whether to open the file in append mode or standard write mode,
   * @return The [[Path]] to the file that was written into
   *
   * @note For internal use by other public utilities
   */
  private def writeToFile(content: String,
                          file: Path,
                          charset: Charset,
                          append: Boolean = false): IO[Path] = {
    val fileFlags = if (!append) Flags.Write else Flags.Append

    // Debug message
    IO {
      logger.debug(s"Writing $content into $file")
    } >>
      // Start single item stream
      Stream.eval(IO.pure(content))
        // Encode string before writing
        .through(text.encode(charset))
        // Write string to file
        .through(Files[IO].writeAll(file, fileFlags))
        // "Run" this Stream and return nothing
        .compile.drain
      // Return the path written into
      >> IO.pure(file)
  }

}
