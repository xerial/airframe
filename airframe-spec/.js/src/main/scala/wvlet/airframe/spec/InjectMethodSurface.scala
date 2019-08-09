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
package wvlet.airframe.spec

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.language.experimental.macros

/**
  *
  */
class InjectMethodSurface extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro InjectMethodSurfaceMacros.impl
}

object InjectMethodSurfaceMacros {

  import scala.reflect.macros.whitebox.Context

  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    val result = annottees map (_.tree) match {
      // Match a class, and expand.
      case (classDef @ q"$mods class $tpname[..$tparams] $ctorMods(...$paramss) extends { ..$earlydefns } with ..$parents { $self => ..$stats }") :: _ =>
        q"""
          $mods class $tpname[..$tparams] $ctorMods(...$paramss) extends { ..$earlydefns } with ..$parents { $self =>
              private[spec] override def methodSurfaces: Seq[wvlet.airframe.surface.MethodSurface] = {
                 wvlet.airframe.surface.Surface.methodsOf[${tpname}]
              }
              ..$stats
            }
          """
      // Not a class.
      case _ => c.abort(c.enclosingPosition, "Invalid annotation target: not a class")
    }

    println(showRaw(result))
    c.Expr[Any](result)
  }
}
