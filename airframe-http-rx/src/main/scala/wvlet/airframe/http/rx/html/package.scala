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
package wvlet.airframe.http.rx

import wvlet.log.LogSupport

import scala.annotation.implicitNotFound
import scala.language.{higherKinds, implicitConversions}

/**
  *
  */
package object html {

  object tags       extends Tags
  object tags_extra extends TagsExtra
  object attrs      extends Attrs

  object all extends Tags with Attrs

  object svgTags  extends SvgTags
  object svgAttrs extends SvgAttrs

  trait HtmlNode {
    def when(cond: => Boolean): HtmlNode = {
      if (cond) this else HtmlNode.empty
    }
    def unless(cond: => Boolean): HtmlNode = {
      if (cond) HtmlNode.empty else this
    }
  }

  // HtmlNode -> Element -> HtmlElement
  //          -> HtmlAttribute

  object HtmlNode {
    object empty extends HtmlNode
  }

  case class HtmlAttribute(name: String, v: Any, ns: Namespace = Namespace.xhtml, append: Boolean = false)
      extends HtmlNode

  class HtmlAttributeOf(name: String, namespace: Namespace = Namespace.xhtml) {
    def apply[V: EmbeddableAttribute](v: V): HtmlNode = HtmlAttribute(name, v, namespace)
    def ->[V: EmbeddableAttribute](v: V): HtmlNode    = apply(v)
    def add[V: EmbeddableAttribute](v: V): HtmlNode   = HtmlAttribute(name, v, namespace, append = true)
    def +=[V: EmbeddableAttribute](v: V): HtmlNode    = add(v)
    def noValue: HtmlNode                             = HtmlAttribute(name, true, namespace)
  }

  case class EntityRef(ref: String) extends HtmlNode

  // TODO embed namespace properly to DOM
  case class Namespace(uri: String)

  object Namespace {
    val xhtml: Namespace = Namespace("http://www.w3.org/1999/xhtml")
    val svg: Namespace   = Namespace("http://www.w3.org/2000/svg")
    val svgXLink         = Namespace("http://www.w3.org/1999/xlink")
  }

  def tag(name: String): HtmlElement                            = new HtmlElement(name)
  def tagOf(name: String, namespace: Namespace)                 = new HtmlElement(name, namespace)
  def attr(name: String): HtmlAttributeOf                       = new HtmlAttributeOf(name)
  def attr(name: String, namespace: Namespace): HtmlAttributeOf = new HtmlAttributeOf(name, namespace)
  def attributeOf(name: String): HtmlAttributeOf                = attr(name)

  @implicitNotFound(msg = "Unsupported type as an attribute value")
  trait EmbeddableAttribute[X]
  object EmbeddableAttribute {
    type EA[A] = EmbeddableAttribute[A]
    @inline implicit def embedNone: EA[None.type]                        = null
    @inline implicit def embedBoolean: EA[Boolean]                       = null
    @inline implicit def embedInt: EA[Int]                               = null
    @inline implicit def embedLong: EA[Long]                             = null
    @inline implicit def embedString: EA[String]                         = null
    @inline implicit def embedFloat: EA[Float]                           = null
    @inline implicit def embedDouble: EA[Double]                         = null
    @inline implicit def embedF0[U]: EA[() => U]                         = null
    @inline implicit def embedF1[I, U]: EA[I => U]                       = null
    @inline implicit def embedOption[C[x] <: Option[x], A: EA]: EA[C[A]] = null
    @inline implicit def embedRx[C[x] <: Rx[x], A: EA]: EA[C[A]]         = null
    @inline implicit def embedSeq[C[x] <: Iterable[x], A: EA]: EA[C[A]]  = null
  }

  @implicitNotFound(msg = "Unsupported type as an HtmlNode")
  trait EmbeddableNode[A]
  object EmbeddableNode extends compat.PlatformEmbeddableNode {
    type EN[A] = EmbeddableNode[A]
    @inline implicit def embedNil: EN[Nil.type]                          = null
    @inline implicit def embedNone: EN[None.type]                        = null
    @inline implicit def embedBoolean: EN[Boolean]                       = null
    @inline implicit def embedInt: EN[Int]                               = null
    @inline implicit def embedChar: EN[Char]                             = null
    @inline implicit def embedShort: EN[Short]                           = null
    @inline implicit def embedByte: EN[Byte]                             = null
    @inline implicit def embedLong: EN[Long]                             = null
    @inline implicit def embedFloat: EN[Float]                           = null
    @inline implicit def embedDouble: EN[Double]                         = null
    @inline implicit def embedString: EN[String]                         = null
    @inline implicit def embedHtmlNode[A <: HtmlNode]: EN[A]             = null
    @inline implicit def embedRx[C[x] <: Rx[x], A: EN]: EN[C[A]]         = null
    @inline implicit def embedSeq[C[x] <: Iterable[x], A: EN]: EN[C[A]]  = null
    @inline implicit def embedOption[C[x] <: Option[x], A: EN]: EN[C[A]] = null
  }

  /**
    * Holder for embedding various types as tag contents
    * @param v
    */
  case class Embedded(v: Any) extends RxElement with LogSupport {
    override def render: RxElement = {
      warn(s"render is called for ${v}")
      ???
    }
  }

  implicit def embedAsNode[A: EmbeddableNode](v: A): RxElement = Embedded(v)

  /**
    * Wraps up a CSS style in a value.
    */
  case class Style(jsName: String, cssName: String) {

    /**
      * Creates an [[StylePair]] from an [[Style]] and a value of type [[T]], if
      * there is an [[StyleValue]] of the correct type.
      */
    def ->[Builder, T](v: T)(implicit ev: StyleValue[Builder, T]) = {
      require(v != null)
      StylePair(this, v, ev)
    }
  }

  /**
    * A [[Style]], it's associated value, and a [[StyleValue]] of the correct type
    */
  case class StylePair[Builder, T](s: Style, v: T, ev: StyleValue[Builder, T]) { //extends Modifier[Builder] {
    override def applyTo(t: Builder): Unit = {
      ev.apply(t, s, v)
    }
  }

  /**
    * Used to specify how to handle a particular type [[T]] when it is used as
    * the value of a [[Style]]. Only types with a specified [[StyleValue]] may
    * be used.
    */
  @implicitNotFound(
    "No StyleValue defined for type ${T}; scalatags does not know how to use ${T} as an style"
  )
  trait StyleValue[Builder, T] {
    def apply(t: Builder, s: Style, v: T): Unit
  }

  @implicitNotFound(
    "No PixelStyleValue defined for type ${T}; scalatags does not know how to use ${T} as an style"
  )
  trait PixelStyleValue[Builder, T] {
    def apply(s: Style, v: T): StylePair[Builder, _]
  }

  /**
    * Wraps up a CSS style in a value.
    */
  case class PixelStyle(jsName: String, cssName: String) {
    val realStyle = Style(jsName, cssName)

    /**
      * Creates an [[StylePair]] from an [[Style]] and a value of type [[T]], if
      * there is an [[StyleValue]] of the correct type.
      */
    def ->[Builder, T](v: T)(implicit ev: PixelStyleValue[Builder, T]) = {
      require(v != null)
      ev(realStyle, v)
    }

  }
  trait StyleProcessor {
    def apply[T](t: T): String
  }

  /**
    * Used to specify how to handle a particular type [[T]] when it is used as
    * the value of a [[HtmlAttribute]]. Only types with a specified [[HtmlAttribute]] may
    * be used.
    */
  @implicitNotFound(
    "No AttrValue defined for type ${T}; scalatags does not know how to use ${T} as an attribute"
  )
  trait AttrValue[Builder, T] {
    def apply(t: Builder, a: HtmlAttribute, v: T): Unit
  }
}
