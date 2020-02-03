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
import sbt.testing.TaskDef
import wvlet.airframe._
import wvlet.airspec.AirSpecSpi
import wvlet.log.LogFormatter.SourceCodeLogFormatter

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

//  def run(spec: AirSpecSpi): Unit = {
//    Task(newSilentDesign + spec.callDesign)
//      .withResource(_.newSession){ session: Session =>
//        Task(spec.callBeforeAll)
//          .andThen()
//
//      }
//      .withResource {
//
//      } { session: Session =>
//        spec.callBeforeAll
//        session
//      }
//      .map {
//
//
//      }
//      .andThen(
//        val childDesign =
//
//      )
//        spec.callBefore
//      }.andThen {
//
//
//    }.andThen {
//        spec.callAfter
//        spec.callAfterAll
//      }
//  }

}
