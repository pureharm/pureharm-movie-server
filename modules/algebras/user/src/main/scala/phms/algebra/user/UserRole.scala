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

package phms.algebra.user

import phms.Fail
import phms._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  */
sealed trait UserRole extends Product with Serializable with Ordered[UserRole] {
  def toName: String = this.productPrefix
}

object UserRole {

  def fromName[F[_]: ApplicativeThrow](s: String): F[UserRole] =
    nameToRole
      .get(s)
      .liftTo[F](Fail.invalid(s"UserRole has to be one of $allString, but was: $s"))

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
