package org.uclm.alarcos.rrc

import java.net.{URI => JavaURI}

import net.sansa_stack.rdf.spark.io.NTripleReader
import net.sansa_stack.rdf.spark.model.{JenaSparkRDDOps, TripleRDD}
import org.apache.spark.sql.{SQLContext, SparkSession}

import scala.collection.mutable

object TripleOps {

  def main(args: Array[String]) = {

    if (args.length < 1) {
      System.err.println(
        "Usage: Triple Ops <input>")
      System.exit(1)
    }
    val input = args(0) //"src/main/resources/rdf.nt"
    val optionsList = args.drop(1).map { arg =>
      arg.dropWhile(_ == '-').split('=') match {
        case Array(opt, v) => (opt -> v)
        case _             => throw new IllegalArgumentException("Invalid argument: " + arg)
      }
    }
    val options = mutable.Map(optionsList: _*)

    options.foreach {
      case (opt, _) => throw new IllegalArgumentException("Invalid option: " + opt)
    }
    println("======================================")
    println("|        Triple Ops example       |")
    println("======================================")

    val sparkSession = SparkSession.builder
      .master("local[*]")
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .appName("Triple Ops example (" + input + ")")
      .getOrCreate()

    val ops = JenaSparkRDDOps(sparkSession.sparkContext)
    import ops._

    val triplesRDD = NTripleReader.load(sparkSession, JavaURI.create(input))

    
    val graph: TripleRDD = triplesRDD
    //Triples filtered by subject ( "http://dbpedia.org/resource/Charles_Dickens" )
    println("All triples related to Dickens:\n" + graph.find(URI("http://dbpedia.org/resource/Charles_Dickens"), ANY, ANY).collect().mkString("\n"))

    //Triples filtered by predicate ( "http://dbpedia.org/ontology/influenced" )
    //println("All triples for predicate hasID:\n" + graph.find(ANY, URI("http://www.semanticweb.org/rrc/ontologies/2017/7/semtweet#hasId"), ANY).collect().mkString("\n"))

    val tweets = graph.find(ANY, URI("http://www.semanticweb.org/rrc/ontologies/2017/7/semtweet#hasId"), ANY)
      .filter(triple => !triple.getSubject().getURI().equals("http://www.semanticweb.org/rrc/ontologies/2017/7/semtweet#Tweet1"))


    println("All tweets :\n" + tweets
      .collect()
      .mkString("\n")
    )

    val allUsers = graph.find(ANY, URI("http://www.semanticweb.org/rrc/ontologies/2017/7/semtweet#hasUser"), ANY)
      .filter(triple => !triple.getObject().getURI().equals("http://www.semanticweb.org/rrc/ontologies/2017/7/semtweet#User1"))

    val sqlContext = new SQLContext(sparkSession.sparkContext)
    println("All users :\n" + allUsers
      .collect()
      .mkString("\n")
    )

    val coords = graph.find(ANY, URI("http://www.semanticweb.org/rrc/ontologies/2017/7/semtweet#hasCoordinates"), ANY)

    println("All with coords :\n" + coords
      .distinct()
      .collect()
      .mkString("\n")
    )

    val hashtags = graph.find(ANY, URI("http://www.semanticweb.org/rrc/ontologies/2017/7/semtweet#hasHashtag"), ANY)

    println("All with hashtags :\n" + hashtags
      .distinct()
      .collect()
      .mkString("\n")
    )


    val df = tweets.map(triple => {triple.getSubject().getURI() + " -- " + triple.getPredicate().getURI() + " -- " +  triple.getObject().toString()})

    println("All triples :\n" + df
      .collect()
      .mkString("\n")
    )


    //Triples filtered by object ( <http://dbpedia.org/resource/Henry_James> )
    println("All triples influenced by Henry_James:\n" + graph.find(ANY, ANY, URI("<http://dbpedia.org/resource/Henry_James>")).collect().mkString("\n"))

    println("Number of triples: " + graph.find(ANY, ANY, ANY).distinct.count())
    println("Number of subjects: " + graph.getSubjects.distinct.count())
    println("Number of predicates: " + graph.getPredicates.distinct.count())
    println("Number of objects: " + graph.getPredicates.distinct.count())

    val subjects = graph.filterSubjects(_.isURI()).collect.mkString("\n")

    val predicates = graph.filterPredicates(_.isVariable()).collect.mkString("\n")
    val objects = graph.filterObjects(_.isLiteral()).collect.mkString("\n")

    //graph.getTriples.take(5).foreach(println(_))

    sparkSession.stop

  }

}