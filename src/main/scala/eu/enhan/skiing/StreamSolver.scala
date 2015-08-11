package eu.enhan.skiing

import akka.stream.scaladsl._
import eu.enhan.skiing.model.{MountainPath, MountainPoint}

/**
 * @author Emmanuel Nhan
 */
object StreamSolver {

  type In = (MountainPoint, Map[(Int,Int), MountainPoint])


  private def balancer[In, MountainPath](worker: Flow[In, MountainPath, Any],workerCount: Int): Flow[In, MountainPath, Unit] = {
    import FlowGraph.Implicits._
    Flow(){ implicit b =>
      val balancer = b.add(Balance[In](workerCount, waitForAllDownstreams = true )) // just to test
    val merge = b.add(Merge[MountainPath](workerCount))

      for( _ <- 1 to workerCount){
        balancer ~> worker ~> merge
      }
      (balancer.in, merge.out)
    }
  }

  private val simpleWorkerFlow = Flow[In].transform(() => new SimpleSolverStage).fold(MountainPath(0, 0, 0, Nil)){(n,acc ) =>
    List(n, acc).max
  }

  val solverFlow = Flow[SkiGraph].map{g =>
      g.maxima.map{maxima => (maxima, g.index)}
    }.mapConcat(identity).via(balancer(simpleWorkerFlow, 100)).fold(MountainPath(0, 0, 0, Nil)){(n,acc ) =>
      List(n, acc).max
    }






}
