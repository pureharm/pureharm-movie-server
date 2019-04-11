---
layout: docs
title: Pure FP
---

# Pure FP (Functional Programming)

Once you've read the section on [Scala](scala.html), it's time to better understand how the language can be used to actually do pure functional programming. For that, I guess, we need to start to understand what pure FP is.

As a great introduction (of this short length) I highly recommend Alexandru Nedelcu's s blog post ["What is Functional Programming?"](https://alexn.org/blog/2017/10/15/functional-programming.html).

## Some concepts

There is some nomenclature that we keep using while talking about pure functional programming, and it's important that you understand it.

### referential transparency
* [referential transparency](https://wiki.haskell.org/Referential_transparency) is one of the cornerstones of functional programming
* [purity](https://en.wikipedia.org/wiki/Pure_function#Pure_functions) is sometimes used interchangeably with "referentially transparent" — justifiably so

### effects
* "effects are good, side-effects are bugs", piece of wisdom from Rob Norris's [scale.bythebay.io 2017 talk](https://www.youtube.com/watch?v=po3wmq4S15A) ~min 17

### side-effects
* the very definition of necessary evil. They do not compose, they can quickly degenerate into spaghetti code that is ridiculously hard to reason about, but we need them because otherwise we'd just be using CPU power for nothing
* the point is to contain them as much as possible, and rely on the good old trick "create a program that describes the side-effects" and then have an "interpreter" apply them
* in the context of web-apps with some sort of HTTP api this means that for your entire request you just build up a program representing what to do in said request, and then, only once, interpret the program to yield the result and send it back over the network

### typeclasses
* [typeclasses](https://blog.scalac.io/2017/04/19/typeclasses-in-scala.html) this is the way to encode typeclasses in Scala.
* [circe](https://github.com/circe/circe), the `json` serialization library. could not work beautifully without it, or serialization/deserialization in general, for that matter

### cats
* [cats](https://github.com/typelevel/cats) stands for "categories"
* we rely on the extended [typelevel](https://github.com/typelevel) ecosystem, of which `cats` is the core
* you should definitely read the book ["Scala with Cats"](https://underscore.io/training/courses/advanced-scala/) by Noel Walsh and David Gurnell

### typeclass derivation
* as a user of this library you don't need to know how to do type-class derivation yourself. But it helps with debugging and avoiding hour-long blocks because you can't get your code to compile
* the de-facto way to do this in Scala is to use [shapeless](https://github.com/milessabin/shapeless)
* an excellent book on shapeless is Dave Gurnell's book ["The Type Astronaut's Guide to Shapeless"](https://underscore.io/training/courses/advanced-shapeless/)
* boiler-plate-less derivation is also routinely done via [def macros](https://docs.scala-lang.org/overviews/macros/overview.html). But the future of this language feature is as of yet slightly uncertain, and should be tracked within the [scalameta](http://scalameta.org/) project. The future de-facto way of doing meta-programming in Scala.

## Things to avoid

### Scala's standard 'Future' 

For modeling any computation that does side-effects, like database-access and so on. Which, unfortunately, in very popular libraries like `slick` is being done. A great shame too, because its pure `DBIOAction` could be easily interpreted into a pure effect instead of `Future`.

To understand why you should avoid `Future`, see:
* [@tpolecat's answer why](https://www.reddit.com/r/scala/comments/3zofjl/why_is_future_totally_unusable/cyns21h/)!
* while you can build decent applications that are almost pure using `Future`, you sacrifice a lot of composability and control over side-effects by doing it
* you should use Scala's `Future` only if you have no other choice, and are forced to by circumstance
* as an alternative to `Future` you should consider something like [monix.eval.Task](https://github.com/monix/monix) or [cats.effect.IO](https://github.com/typelevel/cats-effect). The modules [effects-async](/effects) slightly eases the use of these
* `throw new Exception(...)` is an impure operation! Unless it is immediately captured in a `scala.util.Try`, `cats.effect.IO`, or `monix.eval.Task` you should not use it, and even then be skeptical of what you wrote. Prefer using [Result[T]](/docs/result.html) to model "failure"


