package eu.enhan.skiing

import akka.stream.stage._
import eu.enhan.skiing.model._


/**
 * An implementation of the solver encapsulated into a stage
 * All the work is done inside this stage.
 * @author Emmanuel Nhan
 */
class SimpleSolverStage extends PushPullStage[(MountainPoint, Map[(Int, Int), MountainPoint]), MountainPath]{


  override def onPush(elem: (MountainPoint, Map[(Int, Int), MountainPoint]), ctx: Context[MountainPath]): SyncDirective = {
    // New element to process : compute the solution
    val result = compute(elem._1, elem._2)
    ctx.push(result)
  }

  override def onPull(ctx: Context[MountainPath]): SyncDirective = ctx.pull()



  private def compute(startElem: MountainPoint, index: Map[(Int, Int), MountainPoint]): MountainPath ={

    def innerCompute(n: MountainPoint): MountainPath = {
      n.accessiblePoints match{
        case Nil => // No more points return simple path
          MountainPath(n.z, n.z, 1, List(n))
        case _ => // Pickup the max from accessible nodes
          n.accessiblePoints.map{ child =>
            val p = innerCompute(index(child._1, child._2))
            MountainPath(n.z, p.endHeight, p.length+ 1, n :: p.stack)
          }.max
      }
    }
    innerCompute(startElem)
  }

}


//class KeepTheMaxStage extends DetachedStage[MountainPath, MountainPath]{
//
//
//  override def onPush(elem: MountainPath, ctx: DetachedContext[MountainPath]): UpstreamDirective = {
//
//  }
//
//  override def onPull(ctx: DetachedContext[MountainPath]): DownstreamDirective = {
//    // We only emit downstream if the source upstream is finishing
//    if (ctx.isFinishing){
//      // we can now realease the data
//      if (ctx.isHoldingUpstream)  ctx.pushAndPull() // This should never happen as we are never holding upstream
//      else ctx.push()
//    } else {
//      ctx.holdDownstream()
//    }
//
//  }
//
//  override def onUpstreamFinish(ctx: DetachedContext[MountainPath]): TerminationDirective = ctx.absorbTermination()
//}