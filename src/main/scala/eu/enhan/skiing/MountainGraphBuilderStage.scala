package eu.enhan.skiing

import akka.event.slf4j.Logger
import akka.stream.stage._
import eu.enhan.skiing.model.MountainPoint
import org.slf4j.LoggerFactory


case class SkiGraph(maxima: List[MountainPoint], index: Map[(Int, Int), MountainPoint])

/**
 * @author Emmanuel Nhan
 */
class MountainGraphBuilderStage(val height: Int, val width: Int) extends DetachedStage[MountainPoint, SkiGraph]{

  val log = LoggerFactory.getLogger(classOf[MountainGraphBuilderStage])

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
        if (newPoint.accessiblePoints.length == nbOfAssociatedToConsiderMax(newPoint.x, newPoint.y))
          localMax = newPoint :: localMax
        mountainPoint
      case Some(p) if p.z < mountainPoint.z => // new point is higher only append otherPoint to accessible points
        mountainPoint.copy(accessiblePoints = (p.x, p.y) :: mountainPoint.accessiblePoints)
      case _ => // Nothing to do. No otherPoint or same z
        mountainPoint
    }
  }

  /**
   * Function to deal with the edges
   *
   * @param x
   * @param y
   * @return
   */
  private def nbOfAssociatedToConsiderMax(x: Int, y: Int): Int = {
    val minusOnXMin = if (x == 0) -1 else 0
    val minusOnXMax = if (x == width-1) -1 else 0
    val minusOnYMin = if (y == 0) -1 else 0
    val minusOnYMax = if (y == height-1) -1 else 0
    4 + minusOnXMin + minusOnYMin + minusOnXMax + minusOnYMax
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
      log.info("Upstream is done. Releasing data for downstream")
      ctx.pushAndFinish(SkiGraph(localMax, mountainMap))
    } else {
      ctx.holdDownstream()
    }
  }

  override def onUpstreamFinish(ctx: DetachedContext[SkiGraph]): TerminationDirective = {
    // No questions asked : we absorb termination, as this is it, the streaming of the map is done !
    log.info("Upstream is done")
    ctx.absorbTermination()
  }
}
