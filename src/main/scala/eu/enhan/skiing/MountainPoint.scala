package eu.enhan.skiing

/**
 * A point in the mountain
 *
 * @author Emmanuel Nhan
 */
case class MountainPoint(x: Int, y: Int, z: Int, accessiblePoints: List[(Int, Int)] = Nil)
