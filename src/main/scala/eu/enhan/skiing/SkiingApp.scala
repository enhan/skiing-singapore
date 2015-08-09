package eu.enhan.skiing

import dispatch._
import org.slf4j.LoggerFactory
import org.slf4j.profiler.Profiler
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * @author Emmanuel Nhan
 */
object SkiingApp extends App{
  val log = LoggerFactory.getLogger("app")

  val parser = new MapParser
  val profiler = new Profiler("Downloader")
  profiler.setLogger(log)
  profiler.start("Download & parse")
  val svc = url("http://s3-ap-southeast-1.amazonaws.com/geeks.redmart.com/coding-problems/map.txt")
  Http(svc OK as.String).map{ data =>
    val rawMap = parser.parse(data)
    profiler.stop().print()
    val skiMap = new SkiMap(rawMap)
    log.info("Starting computation")
    val path = skiMap.longestAndSteepestPath

    log.info("Computation done")
    log.info(s"Longest and steepest path is ${path.length} long and drops from ${path.startHeight} to ${path.endHeight} for a drop of ${path.drop}")
    Http.shutdown()
  }

}
