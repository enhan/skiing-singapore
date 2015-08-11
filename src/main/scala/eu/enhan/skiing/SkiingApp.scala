package eu.enhan.skiing

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.stream.{Attributes, ActorMaterializer}
import akka.stream.scaladsl.Source
import org.slf4j.LoggerFactory
import akka.http.scaladsl.client.RequestBuilding._
import org.slf4j.profiler.Profiler

import scala.util.{Failure, Success}


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
  log.info("Downloading & parsing the map")
  val profiler = new Profiler("After download")
  profiler.setLogger(log)


  val f =downloadFlow.transform(() => new MountainGraphBuilderStage(1000, 1000)).map{in =>
    profiler.start("From Zero to hero")
    in
  }.via(
    StreamSolver.solverFlow.withAttributes(Attributes.logLevels(onElement = Logging.WarningLevel))
  ).runForeach{ path =>
    log.info(s"Wining path is ${path.length} long and drops from ${path.startHeight} to ${path.endHeight} for a drop of ${path.drop}")
  }

  f.onComplete{ whatever =>
    profiler.stop().print()
    whatever match {
      case Success(w) =>
        log.info("Completed with success")
      case Failure(ex) =>
        log.error("Error", ex)
    }
    system.shutdown()
  }


}
