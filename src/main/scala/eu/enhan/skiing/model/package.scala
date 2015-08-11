package eu.enhan.skiing

/**
 * @author Emmanuel Nhan
 */
package object model {

  implicit object PathOrdering extends Ordering[MountainPath]{
    override def compare(x: MountainPath, y: MountainPath): Int = {
      val lengthCompare = x.length.compareTo(y.length)
      if (lengthCompare == 0){
        x.drop compareTo y.drop
      } else {
        lengthCompare
      }
    }
  }


}
