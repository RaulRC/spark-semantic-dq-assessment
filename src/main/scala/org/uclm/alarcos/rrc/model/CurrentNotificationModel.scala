package org.uclm.alarcos.rrc.model

import play.api.libs.json.Json

/**
  * Created by Raul Reguillo on 31/08/17.
  */
object CurrentNotificationModel extends Serializable{

  case class CurrentResult(timestamp:Long, currentDischarge: Double, currentCharge:Double)

  object  CurrentResult{
    implicit val formatter = Json.format[CurrentResult]
  }

  case class CurrentNotification(imei: String, metric:String, data: Seq[CurrentResult])

  object CurrentNotification {
    implicit val formatter = Json.format[CurrentNotification]
  }
}
