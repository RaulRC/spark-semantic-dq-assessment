package org.uclm.alarcos.rrc

/**
  * Created by Raul Reguillo on 31/08/17.
  */
case class LogData(log: String)

case class LogLine(log: LogData, stream: String, time: String)