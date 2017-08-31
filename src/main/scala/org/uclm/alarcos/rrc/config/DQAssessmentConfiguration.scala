package org.uclm.alarcos.rrc.config

import java.util.Properties

import com.typesafe.config.Config

/**
  * Created by Raul Reguillo on 30/08/17.
  */
class DQAssessmentConfiguration(env: String, config: Config) extends Serializable{

  val masterMode = config.getString(s"$env.masterMode")

  val hdfsOutputPath = config.getString(s"$env.hdfs.outputPath")
  val hdfsInputPath =  config.getString(s"$env.hdfs.inputPath")

//  val kafkaOutputTopic = config.getString(s"$env.kafka.outputTopic")
//  val kafkaBrokers = config.getString(s"$env.kafka.brokers")
//  val kafkaCompression = config.getString(s"$env.kafka.compression")

}

object DQAssessmentConfiguration {

  val KAFKA_KEY_SERIALIZER = "key.serializer"
  val KAFKA_KEY_SERIALIZER_CLASS = "org.apache.kafka.common.serialization.StringSerializer"
  val KAFKA_VALUE_SERIALIZER = "value.serializer"
  val KAFKA_VALUE_SERIALIZER_CLASS = "org.apache.kafka.common.serialization.StringSerializer"
  val KAFKA_BOOTSTRAP_SERVER = "bootstrap.servers"
  val KAFKA_COMPRESSION_KEY = "compression.type"

  /**
    * Returns the configuration for a specific environment
    * @param env Name of the environment
    * @param config config
    * @return the configuration for Arrowhead steps
    */
  def apply(env:String, config: Config) =
    new DQAssessmentConfiguration(env, config)

  /**
    * Returns the properties to configure the Kafka producer
    * @param broker ip and port of the broker
    * @param compression type of compression (gzip)
    * @return Properties
    */
  def kafkaProperties(broker: String, compression: String): Properties = {
    val props = new Properties()
    props.put(KAFKA_BOOTSTRAP_SERVER, broker)
    props.put(KAFKA_KEY_SERIALIZER, KAFKA_KEY_SERIALIZER_CLASS)
    props.put(KAFKA_VALUE_SERIALIZER, KAFKA_VALUE_SERIALIZER_CLASS)
    props.put(KAFKA_COMPRESSION_KEY, compression)
    props
  }
}