package eu.enhan.skiing

import org.specs2.mutable.Specification

import scala.io.Source

/**
 * @author Emmanuel Nhan
 */
class IntegrationTest extends Specification{

  "Integration" should {
    "be Ok on small 4x4 sample" in {
      val parser = new MapParser
      val skiMap = new SkiMap(parser.parse("4 4\n4 8 7 3\n2 5 9 3\n6 3 2 5\n4 4 1 6"))
      skiMap.solution() must_== "58"
    }

    "be Ok on 10x10 sample" in {
     val rawMap = Source.fromFile("src/test/resources/sample2.txt").mkString

      val parser = new MapParser
      val skiMap = new SkiMap(parser parse rawMap)


      val solution = skiMap.longestAndSteepestPath
      println(solution.stack)

      skiMap.solution() must_== "121000"

    }

  }

}
