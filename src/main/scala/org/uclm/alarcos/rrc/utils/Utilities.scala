package org.uclm.alarcos.rrc.utils

/**
  * Created by marcosvecino on 12/06/17.
  */
object Utilities {

  val formatterDay = DateTimeFormat.forPattern("yyyyMMdd")
  val formatterHour = DateTimeFormat.forPattern("HHmmss")

  /**
    * Returns the timestamp in seconds for a date X months ago
    * @param months Number of months
    * @return
    */
  def secondsForMonthsAgo(months:Int): Long = {
    new DateTime(System.currentTimeMillis()).minusMonths(months).getMillis / 1000
  }

  /**
    * Gets the timestamp in seconds for a day ago for a date
    * @param timestamp date in seconds (unix time)
    * @return timestamp in secods
    */
  def secondsDayAgo(timestamp:Long): Long = {
    new DateTime(timestamp * 1000).minusDays(1).getMillis / 1000
  }

  def outputPath(path:String, stepName: String, period:String): String  = {

    val currentMillis= System.currentTimeMillis()
    val currentDate = formatterDay.print(currentMillis)
    val currentTime = formatterHour.print(currentMillis)

    // Ex /output_path/1M/20170612/103255/
    path.concat(s"/$stepName/$period/$currentDate/$currentTime/")

  }

  def outputJSONPath(path:String, period:String, metricName:String, id:String): String = {

    val currentMillis= System.currentTimeMillis()
    val currentDate = formatterDay.print(currentMillis)
    val currentTime = formatterHour.print(currentMillis)

    // Ex /1M/output_path/20170612/103255/id.json
    path.concat(s"/$period/$currentDate/$currentTime/$metricName/$id.json")

  }




}
