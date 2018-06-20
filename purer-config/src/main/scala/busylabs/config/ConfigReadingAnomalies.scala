package busylabs.config

import busymachines.core.AnomalousFailures
import busymachines.core.AnomalyID
import pureconfig.error.ConfigReaderFailures

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  *
  */
final case class ConfigReadingAnomalies(cs: ConfigReaderFailures)
    extends AnomalousFailures(
      id              = ConfigReadingAnomalies.ID,
      message         = s"Failed to read config file. ${cs.toList.map(_.description).mkString(",")}",
      firstAnomaly    = ConfigReadingAnomaly(cs.head),
      restOfAnomalies = cs.tail.map(ConfigReadingAnomaly.apply)
    )

object ConfigReadingAnomalies {
  case object ID extends AnomalyID { override val name: String = "pms_config_001" }
}
