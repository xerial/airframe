/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wvlet.airframe

import java.util.concurrent.Executors

import wvlet.airframe.surface.Surface
import wvlet.airspec.AirSpec
import wvlet.log.{LogLevel, Logger}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

case class Config1(port: Int = 8080)
case class Config2()

class BuildInFutureTest extends AirSpec {

  private val ex               = Executors.newCachedThreadPool()
  private implicit val context = ExecutionContext.fromExecutor(ex)

  def `capture Surface.of[X] inside Future`: Unit = {
    val cl = Thread.currentThread().getContextClassLoader.loadClass()
    def loop(cl: ClassLoader, indent: Int = 0): Unit = {
      println(s"${" " * indent}-${cl.getClass}")
      cl.getParent match {
        case null =>
        case p: ClassLoader =>
          loop(p, indent + 1)
      }
    }

    loop(cl)
    val f = Future {
      val cl = Thread.currentThread().getContextClassLoader
      warn(s"within Future")
      loop(cl)
      Surface.of[Config1]
    }

    val ret = Await.result(f, Duration.Inf)
    ret.name shouldBe "Config1"
  }

  def `Building in Future causes MISSING_DEPENDENCY` = {
    val f = Future {
      newSilentDesign.build[Config1] { config =>
        debug(config)
      }
    }
    Await.result(f, Duration.Inf)
  }

  def `Building in Future causes java.lang.ClassCastException` = {
    val f = Future {
      newSilentDesign
        .bind[Config2].toInstance(Config2())
        .build[Config1] { config =>
          debug(config)
        }
    }
    Await.result(f, Duration.Inf)
  }
}
