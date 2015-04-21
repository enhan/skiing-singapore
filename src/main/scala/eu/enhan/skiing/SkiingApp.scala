package eu.enhan.skiing

import dispatch._
import org.slf4j.LoggerFactory
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * @author Emmanuel Nhan
 */
object SkiingApp extends App{
  val log = LoggerFactory.getLogger("app")

  val parser = new MapParser

  val svc = url("http://s3-ap-southeast-1.amazonaws.com/geeks.redmart.com/coding-problems/map.txt")
  Http(svc OK as.String).map{ data =>
    val rawMap = parser.parse(data)
    val skiMap = new SkiMap(rawMap)
    log.info("Starting computation")
    val path = skiMap.longestAndSteepestPath
    log.info("Computation done")
    log.info(s"Longest and steepest path is ${path.length} long and drops from ${path.startHeight} to ${path.endHeight} for a drop of ${path.drop}")
    Http.shutdown()
  }

}
