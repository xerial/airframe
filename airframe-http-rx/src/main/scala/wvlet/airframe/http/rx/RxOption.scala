/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this fi
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
package wvlet.airframe.http.rx
import wvlet.airframe.http.rx.Rx.{FilterOp, FlatMapOp, MapOp}

/**
  */
case class RxOption[+A](in: Rx[Option[A]]) extends Rx[A] {
  override def parents: Seq[Rx[_]]                 = Seq(in)
  override def withName(name: String): RxOption[A] = RxOption(in.withName(name))

  override def map[B](f: A => B): RxOption[B] = {
    RxOption(MapOp(in, { x: Option[A] => x.map(f) }))
  }
  override def flatMap[B](f: A => Rx[B]): RxOption[B] = {
    RxOption[B](
      FlatMapOp(
        in,
        { x: Option[A] =>
          x match {
            case Some(v) =>
              f(v).map(Some(_))
            case None =>
              Rx.none
          }
        }
      )
    )
  }

  override def filter(f: A => Boolean): RxOption[A] = {
    RxOption(
      in.map {
        case Some(x) if f(x) => Some(x)
        case _               => None
      }
    )
  }

  override def withFilter(f: A => Boolean): RxOption[A] = filter(f)
}
