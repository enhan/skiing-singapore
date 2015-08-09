package eu.enhan.skiing

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import org.slf4j.LoggerFactory
import akka.http.scaladsl.client.RequestBuilding._
import org.slf4j.profiler.Profiler


/**
 * @author Emmanuel Nhan
 */
object SkiingApp extends App{
  val log = LoggerFactory.getLogger("app")

  implicit val system = ActorSystem("skiing")
  implicit val materializer = ActorMaterializer()
  import system.dispatcher

  val httpFlow = Http().cachedHostConnectionPool[Int](host = "s3-ap-southeast-1.amazonaws.com")
  val downloadFlow = Source.single((Get("/geeks.redmart.com/coding-problems/map.txt"), 0)).via(httpFlow).
    via(StreamMapParser.parser)
  log.info("Dowloading & parsing the map")
  val profiler = new Profiler("Downloader")
  profiler.setLogger(log)
  profiler.start("Download and build raw map")

  val sourceMap = downloadFlow.runFold(Map[(Int, Int), Int]()){ (acc, t) =>  acc + t}

  sourceMap.map{ rawMap =>
    profiler.stop().print()
    val skiMap = new SkiMap(rawMap)
    log.info("Starting computation")
    val path = skiMap.longestAndSteepestPath
    log.info("Computation done")
    log.info(s"Longest and steepest path is ${path.length} long and drops from ${path.startHeight} to ${path.endHeight} for a drop of ${path.drop}")
    system.shutdown()
  }

}
