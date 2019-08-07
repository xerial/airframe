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
package wvlet.airframe.spec.runner
import sbt.testing.{EventHandler, Logger, Task, TaskDef}
import wvlet.airframe.spec.AirSpec
import wvlet.airframe.spec.runner.Framework.AirSpecObjectFingerPrint
import wvlet.airframe.surface.reflect.ReflectTypeUtil
import wvlet.log.LogSupport

/**
  *
  */
class AirTask(override val taskDef: TaskDef, classLoader: ClassLoader) extends sbt.testing.Task with LogSupport {

  override def tags(): Array[String] = Array.empty
  override def execute(eventHandler: EventHandler, loggers: Array[Logger]): Array[Task] = {
    info(s"executing task: ${taskDef}")

    try {
      val className = taskDef.fullyQualifiedName()
      val cls       = classLoader.loadClass(className)

      val testObj = taskDef.fingerprint() match {
        case AirSpecObjectFingerPrint =>
          ReflectTypeUtil.companionObject(cls)
        case _ =>
          Some(cls.newInstance())
      }

      testObj match {
        case Some(as: AirSpec) =>
          info(s"surface: ${as.surface}")
          info(s"ms: ${as.methodSurfaces.mkString("\n")}")
        case other =>
          warn(s"${other.getClass}")
      }
    } catch {
      case e: Throwable =>
        warn(e)
    }
    Array.empty
  }
}
