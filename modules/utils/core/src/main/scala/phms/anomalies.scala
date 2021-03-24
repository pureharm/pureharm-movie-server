/*
 * Copyright 2021 BusyMachines
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package phms

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 09 May 2019
  */
object AnomalyIDS {

  case object NicataAnomalyID extends AnomalyID {
    override val name: String = "NICATA"
  }

  case object IscataAnomalyID extends AnomalyID {
    override val name: String = "ISCATA"
  }

  //--------- movies
  case object MovieNotFoundID extends AnomalyID {
    override val name: String = "m_001"
  }
  //---------
}

/**
  */
object Fail {

  def nicata(what: String): Throwable = Nicata(what)

  def init(what: String): Throwable = Iscata(what, "Invalid initialization values")

  def iscata(what: String, where: String): Throwable = Iscata(what, where)

  def notFound(msg: String): Throwable = NotFoundAnomaly(msg)

  def unauthorized(msg: String): Throwable = UnauthorizedAnomaly(msg)

  def forbidden(msg: String): Throwable = ForbiddenAnomaly(msg)

  def denied(msg: String): Throwable = DeniedAnomaly(msg)

  def invalid(msg: String): Throwable = InvalidInputAnomaly(msg)

  def conflict(msg: String): Throwable = ConflictAnomaly(msg)

  def error(msg: String): Throwable = Catastrophe(msg)
}

/** "Not implemented catastrophe", a slightly better version of stdlib ???
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 09 May 2019
  */
final case class Nicata(what: String)
  extends Catastrophe(
    s"Something is unimplemented: '$what'. Section either in development or it's a complete oversight",
    Option.empty,
  ) {
  override val id: AnomalyID = AnomalyIDS.NicataAnomalyID

  override val parameters: Anomaly.Parameters =
    Anomaly.Parameters("what" -> what)
}

/** "Inconsistent State", technically should never happen, and you should
  * really try to restructure your code so you never get here.
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 09 May 2019
  */
final case class Iscata(what: String, where: String, override val causedBy: Option[Throwable] = None)
  extends Catastrophe(
    s"We have reached some inconsistent state, this is definitely a bug. Where: '$where'. What: '$what'",
    causedBy = causedBy,
  ) {
  override val id: AnomalyID = AnomalyIDS.IscataAnomalyID

  override val parameters: Anomaly.Parameters = Anomaly.Parameters(
    "what"  -> what,
    "where" -> where,
  )
}
