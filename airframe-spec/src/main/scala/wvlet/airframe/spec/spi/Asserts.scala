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
package wvlet.airframe.spec.spi

import wvlet.airframe.SourceCode
import wvlet.airframe.spec._

import scala.reflect.ClassTag

/**
  *
  */
trait Asserts {

  protected def assert(cond: => Boolean)(implicit code: SourceCode) = {
    if (!cond) {
      throw AssertionFailure("assertion failed", code)
    }
  }

  protected def assert(cond: => Boolean, message: String)(implicit code: SourceCode) = {
    if (!cond) {
      throw AssertionFailure(message, code)
    }
  }

  protected def fail(reason: String = "failed")(implicit code: SourceCode): Unit = {
    throw AssertionFailure(reason, code)
  }

  protected def ignore(reason: String = "ignored")(implicit code: SourceCode): Unit = {
    throw Ignored(reason, code)
  }

  protected def pending(reason: String = "pending")(implicit code: SourceCode): Unit = {
    throw Pending(reason, code)
  }

  protected def cancel(reason: String = "cancelled")(implicit code: SourceCode): Unit = {
    throw Cancelled(reason, code)
  }

  protected def skip(reason: String = "skipped")(implicit code: SourceCode): Unit = {
    throw Skipped(reason, code)
  }

  protected def intercept[E <: Throwable: ClassTag](block: => Unit)(implicit code: SourceCode): Unit = {
    val E = implicitly[ClassTag[E]]

    try {
      block
      val name = E.runtimeClass.getName
      throw InterceptException(s"Expected a ${name} to be thrown", code)
    } catch {
      case ex: InterceptException =>
        throw new AssertionFailure(ex.message, code)
      case ex: Throwable if E.runtimeClass.isInstance(ex) =>
      // No-op
    }
  }
}
