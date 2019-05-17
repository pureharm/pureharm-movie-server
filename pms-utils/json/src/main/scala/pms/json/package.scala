package pms

import busymachines.{json => bj}

/**
  *
  * Simply an alias for busymachines.json._ so that we don't have
  * to always import that as well
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
package object json
    extends bj.JsonTypeDefinitions
    with bj.DefaultTypeDiscriminatorConfig {
  type Codec[A] = bj.Codec[A]
  @inline def Codec: bj.Codec.type = bj.Codec
}
