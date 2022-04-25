package org.ragna.comet
package validation.ouputs

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers

class DummyTest extends AsyncFreeSpec with AsyncIOSpec with Matchers {
  "My Code " - {
    "works" in {
      IO(1).asserting(_ shouldBe 1)
    }
  }
}
