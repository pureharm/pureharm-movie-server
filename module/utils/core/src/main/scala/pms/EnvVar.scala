package pms

/** All environment variables that our app uses are kept here for easy lookup,
  * and hopefully for future dhall inter-op.
  */
sealed trait EnvVar extends Product with Serializable {
  def show: String = EnvVar.showEnvVars.show(this)
}

object EnvVar {
  implicit val showEnvVars: Show[EnvVar] = Show.show(_.productPrefix)

  case object PMS_APP_DEV_MODE_BOOTSTRAP extends EnvVar

  case object PMS_SERVER_PORT     extends EnvVar
  case object PMS_SERVER_HOST     extends EnvVar
  case object PMS_SERVER_API_ROOT extends EnvVar

  case object PMS_DB_HOST     extends EnvVar
  case object PMS_DB_PORT     extends EnvVar
  case object PMS_DB_NAME     extends EnvVar
  case object PMS_DB_USERNAME extends EnvVar
  case object PMS_DB_PASSWORD extends EnvVar
  case object PMS_DB_SCHEMA   extends EnvVar

  case object PMS_DB_FLYWAY_CLEAN_ON_VALIDATION extends EnvVar

  case object PMS_EMAIL_FROM      extends EnvVar
  case object PMS_EMAIL_USER      extends EnvVar
  case object PMS_EMAIL_PASSWORD  extends EnvVar
  case object PMS_EMAIL_HOST      extends EnvVar
  case object PMS_EMAIL_PORT      extends EnvVar
  case object PMS_EMAIL_AUTH      extends EnvVar
  case object PMS_EMAIL_START_TLS extends EnvVar

}
