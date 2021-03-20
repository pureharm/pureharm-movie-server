package pms

/** All environment variables that our app uses are kept here for easy lookup,
  * and hopefully for future dhall inter-op.
  */
sealed trait EnvVars extends Product with Serializable {
  def show: String = EnvVars.showEnvVars.show(this)
}

object EnvVars {
  implicit val showEnvVars: Show[EnvVars] = Show.show(_.productPrefix)

  case object PMS_APP_DEV_MODE_BOOTSTRAP extends EnvVars

  case object PMS_SERVER_PORT     extends EnvVars
  case object PMS_SERVER_HOST     extends EnvVars
  case object PMS_SERVER_API_ROOT extends EnvVars

  case object PMS_DB_HOST     extends EnvVars
  case object PMS_DB_PORT     extends EnvVars
  case object PMS_DB_NAME     extends EnvVars
  case object PMS_DB_USERNAME extends EnvVars
  case object PMS_DB_PASSWORD extends EnvVars

  case object PMS_DB_FLYWAY_SCHEMAS extends EnvVars

  case object PMS_EMAIL_FROM      extends EnvVars
  case object PMS_EMAIL_USER      extends EnvVars
  case object PMS_EMAIL_PASSWORD  extends EnvVars
  case object PMS_EMAIL_HOST      extends EnvVars
  case object PMS_EMAIL_PORT      extends EnvVars
  case object PMS_EMAIL_AUTH      extends EnvVars
  case object PMS_EMAIL_START_TLS extends EnvVars

}
