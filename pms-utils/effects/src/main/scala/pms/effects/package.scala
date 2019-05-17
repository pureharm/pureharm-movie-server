package pms

import busymachines.pureharm

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  *
  */
package object effects extends pureharm.effects.PureharmEffectsTypeDefinitions {
  //TODO: move to pureharm
  type Kleisli[F[_], A, B] = cats.data.Kleisli[F, A, B]
  val Kleisli: cats.data.Kleisli.type = cats.data.Kleisli
}
