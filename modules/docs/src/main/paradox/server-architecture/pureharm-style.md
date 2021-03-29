# pureharm style

The principle is quite simple: we create libraries first for any broad domain before using it in our code.

For instance, `phms-util-time` contains _everything_ we need to deal with time in our application, including all relevant type aliases and exports to `java.time` types. So that in application code, we only ever import `phms.time.*` to deal with time. That simple. Some modularization makes perfect sense for libraries, but they become cumbersome to work with in application code.

The most striking example is working with pure fp Scala. At this point, the authors of `pureharm-movie-server` consider `cats`, `cats-effect`, and `fs2` to be basically standard library. So why should we bother importing 80% of the time:

```scala mdoc:silent
import cats._
import cats.implicits._
import cats.effect._
import cats.effect.implicits._
import fs2._
```

When we can just import?

```scala mdoc:silent
import phms._
```

The latter style also allows defining project specific flavors and conventions that are symbiotic w/ the libraries used.