package pms

import busymachines.pureharm.{json => phjson}

/**
  *
  * Simply an alias for busymachines.pureharm.json._ so that we don't have
  * to always import that as well
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
package object json extends phjson.PureharmJsonTypeDefinitions with phjson.DefaultTypeDiscriminatorConfig {
  object implicits extends phjson.PureharmJsonImplicits with busymachines.pureharm.internals.json.AnomalyJsonCodec with JavaTimeJson with PMSCoreJson
  object derive    extends phjson.SemiAutoDerivation

}
