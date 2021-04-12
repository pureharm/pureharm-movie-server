package phms.basics

import phms._
import phms.test._

final class BasicsTest extends PHMSTest {

  /** An email is considered valid if:
    * - it has an '@' character
    * - after the '@' character we have a valid domain name.
    * @return
    *  the string as is, if the input is a valid email.
    */
  def validateEmail[F[_]](email: String)(implicit F: MonadThrow[F]): F[String] = {
    val wheresAt: Option[Int] = email.zipWithIndex.find { case (char, _) => char == '@' }.map(_._2)
    for {
      theAtIndex <- wheresAt.liftTo[F](new RuntimeException("there is no @ in my email"))
      afterThat: String = email.substring(theAtIndex)
      //continue from here
    } yield email
  }

  test("option") {
    val some: Option[String] = Some("I am defined!")
    val none: Option[String] = None
    none match {
      case Some(myString) => println(myString)
      case None           => println("I have none!")
    }

    val doubleSome:  Option[String] = concatString(some, some) //some.map(s => s"$s$s") // concatStrings(some, some)
    val doubleSome2: Option[String] =
      concatString(some, some) //some.map(s => s"$s$s") // concatStrings(some, some)
    val countLength: Option[Int] = doubleSome.map(s => s.length)

    println(s"doubleSome  = $doubleSome")
    println(s"doubleSome2 = $doubleSome2")
    println(s"countLenght = $countLength")
    val anotherSome: Option[String] = Some("I am also defined!")

    val someNone = concatString[Option](some, Option.empty)
    println(s"someNone = $someNone")
    println(for {
      v1 <- Option.empty[String]
      v2 <- Some("1")
      v3 <- Some("1")
      v4 <- Some("1")
    } yield v1 + v2 + v3 + v4)
  }

  test("errors w/ Either") {
    val myStringErr: Attempt[String] = Either.left(new RuntimeException("no string here"))
    val myString:    Attempt[String] = Either.right("I actually have a string!")
    myStringErr match {
      case Left(throwable) => println(s"oh not! I failed w/: $throwable")
      case Right(string)   => println(string)
    }

    println(concatString(myString, myString))
    println(concatString(myStringErr, myString))

    println(concatString[cats.Id]("string1", "string2"))
  }

  def concatStringOldSchool(s1: String, s2: String): String =
    s"$s1 $s2"

  def failOnNegative[F[_]](s1: Int)(implicit F: MonadThrow[F]): F[Int] =
    if (s1 >= 0) s1.pure[F] else new RuntimeException("s is negative").raiseError[F, Int]

  def failOnNegativeAtt(s1: Int): Attempt[Int] =
    if (s1 >= 0) s1.pure[Attempt] else new RuntimeException("s is negative").raiseError[Attempt, Int]
//    if (s1 >= 0) Either.right(s1) else Either.left(new RuntimeException("s is negative"))

  def failOnNegativeOpt(s1: Int): Option[Int] = if (s1 >= 0) Option(s1) else Option.empty

  def concatString[F[_]](f1: F[String], f2: F[String])(implicit F: Monad[F]): F[String] =
    for {
      s1 <- f1
      s2 <- f2
    } yield s"$s1 $s2"

//  def concatStringsAttempt(a1: Attempt[String], a2: Attempt[String]): Attempt[String] =
//    for {
//      s1 <- a1
//      s2 <- a2
//    } yield s"$s1 $s2"
//
//  def concatStringsForOpt(o1: Option[String], o2: Option[String]): Option[String] =
//    for {
//      s1 <- o1
//      s2 <- o2
//    } yield s"$s1 $s2"
//
//  def concatStringsOpt(o1: Option[String], o2: Option[String]): Option[String] = o1.flatMap { s1: String =>
//    o2.map((s2: String) => s"$s1 $s2")
//  }

}
