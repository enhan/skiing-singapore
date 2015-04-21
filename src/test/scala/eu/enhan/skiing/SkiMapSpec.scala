package eu.enhan.skiing

import org.specs2.mutable._

/**
 * @author Emmanuel Nhan
 */
class SkiMapSpec extends Specification{

  "A ski map " should {
    "return the correct longest and steepest path " in {
      val skiMap = new SkiMap(Map(
        (0,0) -> 4,
        (0,1) -> 8,
        (0,2) -> 7,
        (0,3) -> 3,
        (1,0) -> 2,
        (1,1) -> 5,
        (1,2) -> 9,
        (1,3) -> 3,
        (2,0) -> 6,
        (2,1) -> 3,
        (2,2) -> 2,
        (2,3) -> 5,
        (3,0) -> 4,
        (3,1) -> 4,
        (3,2) -> 1,
        (3,3) -> 6
      ))
      skiMap.solution() must_== "58"
    }
  }


}
