package pms.core

import org.specs2.mutable

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
class PhantomTypeSpec extends mutable.Specification {
  import PhantomTypeSpec._

  "PhantomType" >> {
    "should haunt and excorcise the types" >> {
      val original:  String  = "EVERYTHING IS A SPOOK!"
      val haunted:   Haunted = Haunted.haunt(original)
      val exorcised: String  = Haunted.exorcise(haunted)

      original must_=== exorcised
    }
  }
}

object PhantomTypeSpec {
  private[pms] object Haunted extends PhantomType[String]
  private[pms] type Haunted = Haunted.Type
}
