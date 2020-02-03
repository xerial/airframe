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
package wvlet.airspec.runner
import sbt.testing.{OptionalThrowable, TaskDef}
import wvlet.airframe._
import wvlet.airspec.{AirSpecDef, AirSpecSpi, compat}
import wvlet.airspec.spi.{AirSpecContext, AirSpecException}

import scala.concurrent.Future
import scala.util.Try

/**
  *
  */
object AirSpecExecutor {

//  def runTest(
//      taskDef: TaskDef,
//      config: AirSpecConfig,
//      taskLogger: AirSpecLogger,
//      eventHandler: EventHandler,
//      classLoader: ClassLoader
//  ): Unit = {
//
//    /*
//      Task(resource aqcuisition).flatMap{ context =>
//        Task(open session).flatMap { context =>
//          // eval task
//        }
//        .onFinish {
//          // close session
//        }
//      }
//      .onFinish {
//        // release resources
//      }
//     */
//    //
//    //
//    // eval task -> Future[?]
//    //
//
//  }

  def run(parentContext: Option[AirSpecContext], spec: AirSpecSpi, targetDefs: Seq[AirSpecDef]): Task[_] = {

    def init = Task(spec.callBeforeAll).onFinish(_ => spec.callAfterAll)

    def startSpec[A](f: Session => Task[A]): Task[A] = init.andThen {
      val globalDesign = Design.newDesign.noLifeCycleLogging + spec.callDesign
      Task.withResource {
        val s = parentContext
          .map(_.currentSession.newChildSession(globalDesign))
          .getOrElse(globalDesign.newSessionBuilder.noShutdownHook.build)
        s.start
        s
      } { globalSession: Session =>
        f(globalSession)
      }
    }

    def runTest(globalSession: Session, specDef: AirSpecDef): Task[_] = {
      val localDesign = spec.callLocalDesign + specDef.design
      Task(spec.callBefore)
        .onFinish(_ => spec.callAfter)
        .withResource {
          globalSession.newChildSession(localDesign)
        } { childSession: Session =>
          val context =
            new AirSpecContextImpl(
              null, //this,
              parentContext = parentContext,
              currentSpec = spec,
              testName = specDef.name,
              currentSession = childSession
            )
          Task(spec.pushContext(context))
            .onFinish(_ => spec.popContext)
            .andThen {
              val result = specDef.run(context, childSession)
              result match {
                case f: Future[_] => Task.fromFuture(f)
                case _            => Task(result)
              }
              // TODO:  If the test method had any child task, update the flag
              // hadChildTask |= context.hasChildTask
            }
        }
    }

    startSpec { globalSession: Session =>
      val childTasks = for (m <- targetDefs) yield {
        runTest(globalSession, m)
      }
      Task.sequence(childTasks)
    }.onFailure {
      case e: Throwable =>
        //taskLogger.logSpecName(leafName, indentLevel = 0)
        val cause  = compat.findCause(e)
        val status = AirSpecException.classifyException(cause)
        // Unknown error
        val event =
          AirSpecEvent(taskDef, "<spec>", status, new OptionalThrowable(cause), System.nanoTime() - startTimeNanos)
      //taskLogger.logEvent(event)
      //eventHandler.handle(event)
    }

//          Task(spec.callBefore).onFinish(_ => spec.callAfter)
//            .andThen {
//              val localDesign = spec.callLocalDesign + m.design
//              Task.withResource{
//                globalSession.newChildSession(childDesign)
//
//              }
//
//
//            }

    null
  }

}
