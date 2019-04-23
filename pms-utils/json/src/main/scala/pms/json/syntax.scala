package pms.json

import busymachines.json.JsonSyntax
import busymachines.json.SemiAutoDerivation

/**
  *
  * See rationale of [[busymachines.json]]
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
object syntax extends JsonSyntax.Implicits

object derive extends SemiAutoDerivation

object autoderive extends io.circe.generic.extras.AutoDerivation
