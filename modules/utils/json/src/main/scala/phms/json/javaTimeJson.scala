package phms.json

import phms.*
import phms.time.{*, given}

given Encoder[LocalDate] = Encoder[String].contramap((m: LocalDate) => m.show)
given Decoder[LocalDate] = Decoder[String].emapTry(LocalDate.fromString[Try])