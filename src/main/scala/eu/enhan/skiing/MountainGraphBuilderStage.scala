package eu.enhan.skiing

import akka.stream.stage._


case class SkiGraph(maxima: List[MountainPoint], index: Map[(Int, Int), MountainPoint])

/**
 * @author Emmanuel Nhan
 */
class MountainGraphBuilderStage extends DetachedStage[MountainPoint, SkiGraph]{

  private var mountainMap =  Map[(Int, Int), MountainPoint]()
  private var localMax = List[MountainPoint]()

  /**
   * Logic for what it means to process a new node
   *
   * Method with side effects
   *
   * @param mountainPoint
   */
  private def appendNode(mountainPoint: MountainPoint): Unit = {
    // we know parsing is done from the top left corner to the bottom right corner.
    // So we find otherPoint and east for this node if there is any

    val north = mountainMap.get((mountainPoint.x, mountainPoint.y - 1))
    val east = mountainMap.get((mountainPoint.x - 1 , mountainPoint.y))

    val toAppend = setupRelationship(setupRelationship(mountainPoint, north), east)

    mountainMap = mountainMap + ((toAppend.x, toAppend.y) -> toAppend)

  }

  /**
   * setup the relationship with a neighbor node
   *
   * Side effect method !
   */
  private def setupRelationship(mountainPoint: MountainPoint, otherPoint: Option[MountainPoint]): MountainPoint = {
    otherPoint match {
      case Some(p) if p.z > mountainPoint.z => // other is higher
        val newPoint = p.copy(accessiblePoints =  (mountainPoint.x, mountainPoint.y) :: p.accessiblePoints)
        mountainMap = mountainMap + ((p.x, p.y) -> newPoint)
        // check if otherPoint is a local max
        if (newPoint.accessiblePoints.length == 4)
          localMax = newPoint :: localMax
        mountainPoint
      case Some(p) if p.z < mountainPoint.z => // new point is higher only append otherPoint to accessible points
        mountainPoint.copy(accessiblePoints = (p.x, p.y) :: mountainPoint.accessiblePoints)
      case _ => // Nothing to do. No otherPoint or same z
        mountainPoint
    }
  }

  override def onPush(elem: MountainPoint, ctx: DetachedContext[SkiGraph]): UpstreamDirective = {
    // We need to append a new element to the buffer
    appendNode(elem)
    // Ask for more from upstream
    ctx.pull()
  }

  override def onPull(ctx: DetachedContext[SkiGraph]): DownstreamDirective = {
    // We only emit downstream if the source upstream is finishing
    if (ctx.isFinishing){
      // we can now realease the data
      if (ctx.isHoldingUpstream)  ctx.pushAndPull(SkiGraph(localMax, mountainMap)) // This should never happen as we are never holding upstream
      else ctx.push(SkiGraph(localMax, mountainMap))
    } else {
      ctx.holdDownstream()
    }
  }

  override def onUpstreamFinish(ctx: DetachedContext[SkiGraph]): TerminationDirective = {
    // No questions asked : we absorb termination, as this is it, the streaming of the map is done !
    ctx.absorbTermination()
  }
}
