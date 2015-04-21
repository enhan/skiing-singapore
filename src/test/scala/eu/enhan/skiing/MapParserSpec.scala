package eu.enhan.skiing

import org.specs2.mutable._

/**
 * @author Emmanuel Nhan
 */
class MapParserSpec extends Specification{

  "A map parser" should {
    val parser = new MapParser()
    "Parse correctly" in {
      val rawMap = parser.parse("4 4\n4 8 7 3\n2 5 9 3\n6 3 2 5\n4 4 1 6")
      rawMap must_== Map(
        (0, 0) -> 4,
        (0, 1) -> 8,
        (0, 2) -> 7,
        (0, 3) -> 3,
        (1, 0) -> 2,
        (1, 1) -> 5,
        (1, 2) -> 9,
        (1, 3) -> 3,
        (2, 0) -> 6,
        (2, 1) -> 3,
        (2, 2) -> 2,
        (2, 3) -> 5,
        (3, 0) -> 4,
        (3, 1) -> 4,
        (3, 2) -> 1,
        (3, 3) -> 6
      )
    }
  }

}
