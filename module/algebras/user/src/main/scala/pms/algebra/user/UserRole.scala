package pms.algebra.user

import pms.core.Fail
import pms.effects._
import pms.effects.implicits._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  *
  */
sealed trait UserRole extends Product with Serializable with Ordered[UserRole]

object UserRole {

  def fromName(s: String): Attempt[UserRole] =
    nameToRole
      .get(s)
      .liftTo[Attempt](Fail.invalid(s"UserRole has to be one of $allString, but was: $s"))

  implicit val userRoleOrdering: Ordering[UserRole] =
    (x: UserRole, y: UserRole) => x.compare(y)

  private val LT = -1
  private val EQ = 0
  private val GT = 1

  case object Newbie extends UserRole {

    override def compare(that: UserRole): Int = that match {
      case Newbie => EQ
      case _      => LT
    }
  }

  case object Member extends UserRole {

    override def compare(that: UserRole): Int = that match {
      case Newbie => GT
      case Member => EQ
      case _      => LT
    }
  }

  case object Curator extends UserRole {

    override def compare(that: UserRole): Int = that match {
      case Newbie  => GT
      case Member  => GT
      case Curator => EQ
      case _       => LT
    }
  }

  case object SuperAdmin extends UserRole {

    override def compare(that: UserRole): Int = that match {
      case Newbie     => GT
      case Member     => GT
      case Curator    => GT
      case SuperAdmin => EQ
    }
  }

  private lazy val all: Set[UserRole] = Set(
    Newbie,
    Member,
    Curator,
    SuperAdmin,
  )

  private lazy val nameToRole: Map[String, UserRole] =
    all.map(s => (s.productPrefix, s)).toMap

  private lazy val allString = all.mkString("[", ",", "]")
}
