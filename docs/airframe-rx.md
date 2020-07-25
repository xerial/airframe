---
id: airframe-rx
title: Airframe Rx
---

## Reactive HTML 

airframe-rx is a reactive DOM builder for Scala.js.


## Why Airframe Rx? 

Scala is a functional programming language. Airframe Rx supports rendering DOM elements by nesting Scala functions.    


```scala
import wvlet.airframe.http.rx.html.all._

// Creating a div element
div(cls -> "main", "Hello Airframe Rx Widget!")

// Using loop for rendering a table
table(
  cls -> "table",
  tr(
    (0 to 5).map { i =>
      td(s"col ${i}")  
    }
  )
)
```



## Rendering DOM

To render DOM elements, `import wvlet.airframe.http.rx.html.all._`:
 
Standard HTML elements (e.g., div, span, a, table, th, tr, etc.) are defined in this package.


```scala
import wvlet.airframe.http.rx.html.DOMRenderer
import wvlet.airframe.http.rx.html.all._

val main = document.getElementById("main")
val content = div(cls -> "container", span("Hello Airframe Rx!"))

// Render the DOM to the target HTMLElement
DOMRenderer.renderTo(main, content)
```

`->` can be used for setting an attribute value, and `+=` can be used for appending other attribute values. 

For setting CSS classes, 

div(

)


### RxElement


### RxComponent





## Example

__index.html__

```html
<body>
  <div id="main"/>
  <script type="text/javascript" src="(your project)-fastopt.js"></script>
</body> 
```

__UIMain.scala__

```scala
object UIMain {
  @JSExport
  def main(): Unit = {
    val main = document.getElementById("main")
    DOMRenderer.renderTo(main, content)
  }
}
```


