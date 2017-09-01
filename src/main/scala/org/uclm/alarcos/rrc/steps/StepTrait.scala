package org.uclm.alarcos.rrc.steps

import org.apache.log4j.{LogManager, Logger}
import org.apache.spark.sql._


/**
  * Created by Raul Reguillo on 31/08/17.
  */
trait StepTrait extends Serializable{

  protected val log: Logger = LogManager.getLogger(this.getClass.getName)
  protected val processSparkSession: SparkSession

  def execute()

  /**
    * Gets a dataframe from parquet data
    *
    * @param path Path in HDFS with the parquet data
    * @return Dataframe
    */
  def getDataframeFromParquet(path:String): DataFrame = {
    log.info(s"Load DataFrame from Parquet $path")
    try {
      processSparkSession.read.option("mergeSchema", "false").parquet(path)

//      val conf = processSparkSession.sparkContext.hadoopConfiguration
//      val fs = FileSystem.get(conf)
//      val hfpath = new Path(path)

//      if (fs.exists(hfpath))
//        processSparkSession.read.option("mergeSchema", "false").parquet(path)
//      else {
//        import processSparkSession.implicits._
//        processSparkSession.emptyDataset[ArrowheadData].toDF
//      }

    } catch {
      case e: Exception =>
        log.error(e.getMessage)
        throw e
    }
  }

  /**
    * Gets a dataframe from avro data
    *
    * @param path Path in HDFS with the avro data
    * @return Dataframe
    */
  def getDataframeFromAvro(path:String): DataFrame = {
    log.info(s"Load DataFrame from Avro $path")
    try
      processSparkSession.read.format("com.databricks.spark.avro").load(path)

    catch {
      case e: Exception =>
        log.error(e.getMessage)
        throw e
    }
  }

  def getDataframeFromJSON(path:String): DataFrame = {
    log.info(s"Load DataFrame from JSON $path")
    try
      processSparkSession.read.json(path)

    catch {
      case e: Exception =>
        log.error(e.getMessage)
        throw e
    }
  }

  /**
    * Writes a Dataframe as avro data
    *
    * @param df Dataframe to write
    * @param path Path in HDFS
    * @param mode Save mode
    */
  def writeDataframeAsAvro(df:DataFrame, path:String, mode:SaveMode) = {
    log.info(s"Write DataFrame as Avro $path")
    try
      df.write.format("com.databricks.spark.avro").mode(mode).save(path)

    catch {
      case e: Exception =>
        log.error(e.getMessage)
        throw e
    }
  }

//  /**
//    * Writes a Dataframe as parquet data
//    *
//    * @param df Dataframe to write
//    * @param path Path in HDFS
//    * @param mode Save mode
//    */
//  def writeDataframeAsParquet(df:DataFrame, path:String, mode: SaveMode) = {
//    log.info(s"Write DataFrame as Parquet $path")
//    try{
//        df.write.mode(mode).parquet(path)
//
//    } catch {
//      case e: Exception =>
//        log.error(e.getMessage)
//        throw e
//    }
//  }

//  /**
//    * Writes a Dataframe as a CSV
//    *
//    * @param df Dataframe to write
//    * @param path Path in HDFS
//    * @param mode Save mode
//    */
//  def writeDataframeAsCSV(df:DataFrame, path:String, mode: SaveMode) = {
//    log.info(s"Write DataFrame as CSV $path")
//    try
//      df.write.option("header", "true").option("codec", "gzip").mode(mode).csv(path)
//
//    catch {
//      case e: Exception =>
//        log.error(e.getMessage)
//        throw e
//    }
//  }

//  /**
//    *  Writes a JSON file in HDFS
//    *
//    * @param data String to write
//    * @param path Path in HDFS
//    */
//  def writeJsonHDFS(data:String, path:String) = {
//    log.info(s"Writing JSON in $path")
//
//    try {
//
//      val conf = processSparkSession.sparkContext.hadoopConfiguration
//      val fs = FileSystem.get(conf)
//
//      val output = fs.create(new Path(path))
//      output.write(data.getBytes)
//      output.close()
//
//    } catch {
//      case e: IOException =>
//        log.error(e.getMessage)
//        throw e
//    }
//  }

}