package eu.enhan.skiing

import org.slf4j.LoggerFactory

/**
 * @author Emmanuel Nhan
 */
class MapParser {
  val log = LoggerFactory.getLogger(classOf[MapParser])

  def parse(rawString: String): Map[(Int, Int), Int] = {
    log.debug("Starting parsing of the map")
    val lines = rawString.split("\n")
    val mapDataLines = lines.slice(1, lines.length)
    // first line are dimensions
    val dimensions = lines(0).split(" ").map(_.toInt)
    val height = dimensions(0)
    val width = dimensions(1)
    log.debug(s"Map size is ${height}x$width")
    val tuples = for {
      (line, i) <- mapDataLines.zipWithIndex
      (cell, j) <- line.split(" ").zipWithIndex
    } yield {
        (i, j) -> cell.toInt
    }
    val result = tuples.toMap
    log.debug("Parsing done")
    result
  }

}
