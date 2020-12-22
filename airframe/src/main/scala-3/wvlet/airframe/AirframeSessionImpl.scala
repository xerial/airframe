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

import java.util.concurrent.ConcurrentHashMap

import wvlet.airframe.AirframeException.{CYCLIC_DEPENDENCY, MISSING_DEPENDENCY}
import wvlet.airframe.Binder._
import wvlet.airframe.lifecycle.{CloseHook, EventHookHolder, Injectee, LifeCycleManager}
import wvlet.airframe.surface.Surface
import wvlet.airframe.tracing.{DIStats, DefaultTracer, Tracer}
import wvlet.log.LogSupport

import scala.jdk.CollectionConverters._
import scala.util.Try

private[airframe] trait AirframeSessionImpl { self: AirframeSession =>
   def register[A](instance: A): Unit = ???
/*   
    val surface = Surface.of[A]
    val owner   = findOwnerSessionOf(surface).getOrElse(this)
    owner.registerInjectee(surface, surface, instance)
  }
  */
}