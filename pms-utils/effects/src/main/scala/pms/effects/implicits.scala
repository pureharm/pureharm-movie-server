package pms.effects

import busymachines.pureharm

/**
  *
  * !!!!! WARNING !!!!!
  *
  * DO NOT import this together with any of:
  * {{{
  * cats.implicits._
  * cats.syntax.X._
  * cats.instances._
  * cats.effect.implicits._
  * }}}
  *
  * This object already provides all of those + some extra syntax
  *
  * !!!!!!!!!!!!!!!!!!!
  *
  * Any pure-movie-server specific syntax should go here
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 17 May 2019
  *
  */
object implicits extends EffectsSyntax.Implicits with pureharm.effects.PureharmEffectsAndCatsImplicits
