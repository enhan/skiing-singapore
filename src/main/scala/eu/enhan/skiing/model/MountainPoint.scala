package eu.enhan.skiing.model

/**
 * A point in the mountain
 *
 * @author Emmanuel Nhan
 */
case class MountainPoint(x: Int, y: Int, z: Int, accessiblePoints: List[(Int, Int)] = Nil)

case class MountainPath(startHeight: Int, endHeight: Int, length: Int, stack: List[MountainPoint]){
  def drop = startHeight - endHeight
}
