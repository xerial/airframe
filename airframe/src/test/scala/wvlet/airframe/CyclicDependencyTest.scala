package wvlet.airframe

import wvlet.airspec._
import wvlet.airframe.AirframeException.CYCLIC_DEPENDENCY

object CyclicDependencyTest extends AirSpec {

  trait A {
    val b = bind[B]
  }

  trait B {
    val a = bind[A]
  }

  def `report cyclic dependency`: Unit = {
    val d = newSilentDesign
    intercept[CYCLIC_DEPENDENCY] {
      d.build[A] { a =>
        // do nothing
      }
    }
  }
}
