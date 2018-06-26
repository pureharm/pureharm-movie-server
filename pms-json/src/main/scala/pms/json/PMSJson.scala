package pms.json

import busymachines.json.AnomalyJsonCodec

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
trait PMSJson extends JavaTimeJson with PMSCoreJson with AnomalyJsonCodec
