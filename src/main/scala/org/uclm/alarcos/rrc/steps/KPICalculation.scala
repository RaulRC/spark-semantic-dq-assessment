package org.uclm.alarcos.rrc.steps

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.io.LongWritable
import org.apache.jena.hadoop.rdf.io.input.rdfxml.RdfXmlInputFormat
import org.apache.jena.hadoop.rdf.io.registry.HadoopRdfIORegistry
import org.apache.jena.hadoop.rdf.io.registry.readers.RdfXmlReaderFactory
import org.apache.jena.hadoop.rdf.types.TripleWritable
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.uclm.alarcos.rrc.config.DQAssessmentConfiguration
import org.uclm.alarcos.rrc.utils.Utilities


/**
  * Created by raul.reguillo on 31/08/17.
  */
class KPICalculation(config: DQAssessmentConfiguration,
                     sparkSession: SparkSession,
                     period: String) extends StepTrait{

  protected val processSparkSession: SparkSession = sparkSession

  import processSparkSession.implicits._

  def execute(): Unit = {
    try {

      val stepName = "KPIs"

      // Match period
      val (timeWindow, hoursPeriod, seconds) = period match {
        case "1M" => ("4 weeks", 672, Utilities.secondsForMonthsAgo(13))
        case "1W" => ("1 week", 168, Utilities.secondsForMonthsAgo(5))
        case "1D" => ("1 day", 24, Utilities.secondsForMonthsAgo(4))
        case "1H" => ("1 hour", 1, Utilities.secondsForMonthsAgo(2))
        case _ => ("1 hour", 1, Utilities.secondsForMonthsAgo(2))
      }

      //Load and custom synthetic dataset

      //INPUT
      val df = sparkSession.read.textFile(config.hdfsInputPath + "*")
      val df2 = sparkSession.read.format("com.databricks.spark.xml")
        .option("rowTag", "book")
        .load(config.hdfsInputPath + "sample_output.xml")

      //PROCESS
      df.show(10)
      df2.show(10, truncate=false)
      val ordered = df.map(line  => line.replace("{", "").replace("}", "").split(",").sortWith(_ > _))
      val lines = df.count()
      val words = df.flatMap(line => line.split(" "))
        .map(word => (word, 1))
      words.show(10)

      val factory = new RdfXmlReaderFactory()
      HadoopRdfIORegistry.addReaderFactory(factory)
      val conf = new Configuration()
      conf.set("rdf.io.input.ignore-bad-tuples", "false")
      val data = sparkSession.sparkContext.newAPIHadoopFile(config.hdfsInputPath,
        classOf[RdfXmlInputFormat],
        classOf[LongWritable], //position
        classOf[TripleWritable],   //value
        conf)

      data.take(10).foreach(println)
      //OUTPUT
      words.write.mode(SaveMode.Overwrite).save(config.hdfsOutputPath + "out")

    }
    catch {
      case e: Exception =>
        val msg = s"Error in step: $e"
        e.printStackTrace()
        log.error(msg)
    }
  }
}

