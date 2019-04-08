package pms.config

import busymachines.core._
import pureconfig.error.ConfigReaderFailure

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  *
  */
final case class ConfigReadingAnomaly(c: ConfigReaderFailure)
    extends InvalidInputFailure(s"Failed to read config because: ${c.description}") {

  override def id: AnomalyID = ConfigReadingAnomaly.ID

  override def parameters: Anomaly.Parameters = {
    val orig: Anomaly.Parameters = Anomaly.Parameters(
      "reason" -> c.description,
    )
    val loc = c.location.map(l => ("location" -> l.description): (String, Anomaly.Parameter))
    orig.++(loc.toMap: Anomaly.Parameters)
  }
}

object ConfigReadingAnomaly {
  case object ID extends AnomalyID { override val name: String = "pms_config_002" }
}
