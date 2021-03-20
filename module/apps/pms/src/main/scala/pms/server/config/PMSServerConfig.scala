package pms.server.config

import pms._
import pms.config._
import pms.algebra.imdb.IMDBAlgebraConfig
import pms.db.config.DatabaseConfig
import pms.email.GmailConfig

final case class PMSServerConfig(
  httpConfig:  HttpConfig,
  emailConfig: GmailConfig,
  imdbConfig:  IMDBAlgebraConfig,
  dbConfig:    DatabaseConfig,
)

object PMSServerConfig {

  def resource[F[_]: Monad: Config]: Resource[F, PMSServerConfig] =
    for {
      serverConfig      <- HttpConfig.resource[F]
      gmailConfig       <- GmailConfig.resource[F]
      imdbAlgebraConfig <- IMDBAlgebraConfig.resource[F]
      dbConfig          <- DatabaseConfig.resource[F]
    } yield PMSServerConfig(
      serverConfig,
      gmailConfig,
      imdbAlgebraConfig,
      dbConfig,
    )

}
