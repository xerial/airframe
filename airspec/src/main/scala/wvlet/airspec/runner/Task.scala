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

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success}
import scala.util.control.NonFatal

/**
  *
  */
trait Cancelable {
  def cancel: Unit = {}
}

trait Task[A] extends Cancelable {
  import Task._

  def eval(implicit context: ExecutionContext): Future[A]
  def map[B](f: A => B): Task[B]           = MapOp(this, f)
  def flatMap[B](f: A => Task[B]): Task[B] = FlatMapOp(this, f)
  def andThen[B](next: Task[B]): Task[B]   = new Task.AndThen(this, next)

  //def withResource[R <: AutoCloseable, B](r: A => R)(f: R => Task[B]): Task[B] = WithResource(resource)
  //def rescue[U](pf: PartialFunction[Throwable, U]): Task[In, U]
}

/**
  *
  */
object Task {
  def apply[A](v: => A): Task[A] = new Task[A] {
    override def eval(implicit executionContext: ExecutionContext): Future[A] = {
      Future.successful(v)
    }
  }

  def withResource[R <: AutoCloseable, A](init: => R)(f: R => Task[A]): Task[A] =
    new WithResource[R, A](resource = () => init, body = f)

  def fromFuture[A](f: scala.concurrent.Future[A]): Task[A] = new Task[A] {
    override def eval(implicit executionContext: ExecutionContext): Future[A] = {
      f
    }
  }

  private case class AndThen[A, B](prev: Task[A], next: Task[B]) extends Task[B] {
    override def eval(implicit executionContext: ExecutionContext): Future[B] = {
      try {
        prev.eval
          .flatMap { x =>
            next.eval
          }
      } catch {
        case NonFatal(e) => Future.failed[B](e)
      }
    }
  }

  private case class MapOp[A, B](prev: Task[A], f: A => B) extends Task[B] {
    override def eval(implicit executionContext: ExecutionContext): Future[B] = {
      prev.eval.map(x => f(x))
    }
  }
  private case class FlatMapOp[A, B](prev: Task[A], f: A => Task[B]) extends Task[B] {
    override def eval(implicit context: ExecutionContext): Future[B] = {
      prev.eval.flatMap { a =>
        f(a).eval
      }
    }
  }

  private case class WithResource[R <: AutoCloseable, A](resource: () => R, body: R => Task[A]) extends Task[A] {
    override def eval(implicit context: ExecutionContext): Future[A] = {
      val p = Promise[A]()
      val r = resource()
      body(r).eval.onComplete {
        case Success(a) =>
          p.success(a)
        case Failure(e) =>
          p.failure(e)
          r.close()
      }
      p.future
    }
  }

}
