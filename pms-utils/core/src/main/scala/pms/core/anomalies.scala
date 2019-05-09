package pms.core

import busymachines.core._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 09 May 2019
  *
  */
object AnomalyIDS {
  case object NicataAnomalyID extends AnomalyID { override val name: String = "NICATA" }
  case object IscataAnomalyID extends AnomalyID { override val name: String = "ISCATA" }

  //--------- movies
  case object MovieNotFoundID extends AnomalyID { override val name: String = "m_001" }
  //---------
}

/**
  *
  *
  */

object Fail {

  def nicata(what: String): Throwable = Nicata(what)

  def iscata(what: String, where: String): Throwable = Iscata(what, where)

  def notFound(msg: String): Throwable = NotFoundFailure(msg)

  def unauthorized(msg: String): Throwable = UnauthorizedFailure(msg)

  def forbidden(msg: String): Throwable = ForbiddenFailure(msg)

  def denied(msg: String): Throwable = DeniedFailure(msg)

  def invalid(msg: String): Throwable = InvalidInputFailure(msg)

  def conflict(msg: String): Throwable = ConflictFailure(msg)

  def error(msg: String): Throwable = CatastrophicError(msg)
}

/**
  *
  * "Not implemented catastrophe", a slightly better version of stdlib ???
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 09 May 2019
  *
  */
final case class Nicata(what: String)
    extends CatastrophicError(
      s"Something is unimplemented: '$what'. Section either in development or it's a complete oversight",
    ) {
  override val id: AnomalyID = AnomalyIDS.NicataAnomalyID

  override val parameters: Anomaly.Parameters = Anomaly.Parameters("what" -> what)
}

/**
  *
  * "Inconsistent State", technically should never happen, and you should
  * really try to restructure your code so you never get here.
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 09 May 2019
  *
  */
final case class Iscata(what: String, where: String, causedBy: Option[Throwable] = None)
    extends CatastrophicError(
      s"We have reached some inconsistent state, this is definitely a bug. Where: '$where'. What: '$what'",
      causedBy = causedBy,
    ) {
  override val id: AnomalyID = AnomalyIDS.IscataAnomalyID

  override val parameters: Anomaly.Parameters = Anomaly.Parameters(
    "what"  -> what,
    "where" -> where,
  )
}
