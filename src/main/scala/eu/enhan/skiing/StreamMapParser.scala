package eu.enhan.skiing

import akka.http.scaladsl.model.HttpResponse
import akka.stream.FlowShape
import akka.stream.io.Framing
import akka.stream.scaladsl._
import akka.util.ByteString
import eu.enhan.skiing.model.MountainPoint

import scala.util.{Failure, Success, Try}

/**
 * @author Emmanuel Nhan
 */
object StreamMapParser {


  private val byteMapper = Flow[(Try[HttpResponse], Int)].map{ in =>
    val (t, i) = in
    t match {
      case Success(response) => response
    }
  }.map{
    _.entity.dataBytes
  }.flatten(FlattenStrategy.concat).
    via(Framing.delimiter(ByteString("\n"), 1000000)).
    map(_.utf8String).
    drop(1)


  val parser = Flow.wrap{
    FlowGraph.partial(byteMapper){ implicit b => mapper =>
      import FlowGraph.Implicits._

      val s = Source(Stream.from(0))
      val merge = b.add(Zip[Int, String]())
      val tupleBuilder = b.add(Flow[(Int, String)].map{ t =>
        val (i, line) = t
        val a = for{
          (cell, j) <- line.split(" ").zipWithIndex
        } yield {
          MountainPoint(i, j, cell.toInt)
        }
        a.toList
      }.mapConcat(identity))

      s ~> merge.in0 ; merge.out ~> tupleBuilder
      mapper.outlet ~> merge.in1

      new FlowShape(mapper.inlet, tupleBuilder.outlet)
    }
  }

}

