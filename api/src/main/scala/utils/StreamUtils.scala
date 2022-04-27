package org.ragna.comet
package utils

import cats.effect.IO
import fs2.Pipe

/**
 * Utilities to work with FS2 streams
 */
object StreamUtils {

  /**
   * Stream pipe for printing any items to Console
   *
   * @note Using evalTap, we do not change the Stream type/contents
   *       in the transformation,
   *       we just debug them and leave them as they are
   */
  def toConsole[A]: Pipe[IO, A, A] =
    inputStream => inputStream.evalTap(IO.pure(_).debug)

}
