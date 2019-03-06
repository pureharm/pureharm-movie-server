package pms.core

import shapeless.tag.@@

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  *
  */
trait PhantomType[T] {  ///explanations needed

  type Phantom <: this.type

  type Raw  = T
  type Type = T @@ Phantom

  @inline def apply(value: T): @@[T, Phantom] =
    shapeless.tag[Phantom](value)

  /**
    * alias for [[apply]]
    */
  @inline def haunt(value: T): @@[T, Phantom] =
    shapeless.tag[Phantom](value)

  @inline def exorcise(phantom: Type): T =
    identity(phantom)
}
