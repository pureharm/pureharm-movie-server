//package pms.email
//
//import busymachines.effects._
//import org.scalatest.FunSpec
//
///**
//  *
//  * @author Lorand Szakacs, https://github.com/lorandszakacs
//  * @since 05 Jun 2018
//  *
//  */
//class EmailTest extends FunSpec {
//  private def test: ItWord = it
//
//  describe("Gmail service implementation") {
//    val config = GmailConfig(
//      from     = "john.busylabs@gmail.com",
//      user     = "john.busylabs@gmail.com",
//      password = "]6|F;o2HPx/85-}BPgDo",
//      host     = "smtp.gmail.com",
//      port     = 587,
//      auth     = true,
//      startTLS = true
//    )
//
//    test("create service IO + send email") {
//      val gmail = EmailService.gmailClient[IO](config)
//
//      val io: IO[Unit] = for {
//        to <- Email("lorand.szakacs@busymachines.com").asIO
//        _  <- gmail.sendEmail(to, "BusylabsTest[IO]", "Yey!")
//      } yield ()
//
//      io.unsafeRunSync()
//    }
//
//    test("create service Task + send email") {
//      implicit val scheduler: Scheduler = Scheduler.global
//      val gmail = EmailService.gmailClient[Task](config)
//
//      val io: Task[Unit] = for {
//        to <- Email("lorand.szakacs@busymachines.com").asTask
//        _  <- gmail.sendEmail(to, "BusylabsTest[Task]", "Yey!")
//      } yield ()
//
//      io.runSyncUnsafe(busymachines.duration.minutes(1))
//    }
//
//  }
//}
