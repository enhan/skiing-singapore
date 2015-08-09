package eu.enhan.skiing

import org.slf4j.LoggerFactory
import org.slf4j.profiler.Profiler

/**
 * @author Emmanuel Nhan
 */
class SkiMap(val rawMap: Map[(Int, Int), Int]) {

  val log = LoggerFactory.getLogger(classOf[SkiMap])
  lazy val longestAndSteepestPath = computeLongestAndSteepestPath()
  lazy val allPaths = findAllPath()

  implicit object PathOrdering extends Ordering[Path]{
    override def compare(x: Path, y: Path): Int = {
      val lengthCompare = x.length.compareTo(y.length)
      if (lengthCompare == 0){
        x.drop compareTo y.drop
      } else {
        lengthCompare
      }
    }
  }

  def feeder( source:() => Map[(Int, Int), Tile])( x: Int, y: Int): ()=>List[Tile] = () => {

    def north: Option[Tile] = source().get(x-1, y)
    def south: Option[Tile] = source().get(x+1, y)
    def east: Option[Tile] = source().get(x, y-1)
    def west: Option[Tile] = source().get(x, y+1)
    List(north, south, east, west).flatten
  }

  lazy val fullMap: Map[(Int, Int), Tile ] = rawMap.map{
    case ((x, y), h) => (x, y) -> Tile(x, y, h, feeder(() =>fullMap)(x, y))
  }

  private def findAllPath(): Iterable[Path] = {
    val profiler = new Profiler("PATH FINDER")
    profiler.setLogger(log)
    profiler.start("BUILD SOLUTIONS FOR ALL TILES")
    val paths = fullMap.values.map{tile => tile.longestAndSteepestPath}
    profiler.stop().print()
    paths
  }

  private def computeLongestAndSteepestPath(): Path = {
    val profiler = new Profiler("Find best solution")
    profiler.start("FIND MAX")
    val res = allPaths.max
    profiler.stop().print()
    res
  }

  def solution() = {
    val result = longestAndSteepestPath
    s"${result.length}${result.drop}"
  }

  def pathFor(i: Int, j: Int) = fullMap((i, j)).longestAndSteepestPath

  def sortedResults = allPaths.toList.sorted

  case class Path(startHeight: Int, endHeight: Int, length: Int, stack: List[Tile]){
    def drop = startHeight - endHeight
  }

  case class Tile(x: Int, y: Int, height: Int, neighbors: () => List[Tile]) {

    lazy val accessibleNeighbors =  neighbors.apply().filter(n => n.height < height)
    lazy val longestAndSteepestPath = computeLongestAndSteepestPath()

    private def computeLongestAndSteepestPath(): Path = {
      accessibleNeighbors match{
        case Nil => Path(height, height, 1, List(this))
        case _ => accessibleNeighbors.map{n =>
            val p = n.computeLongestAndSteepestPath()
            // Include this node
            Path(height, p.endHeight, p.length + 1, this :: p.stack)
          }.max
      }
    }
  }
}




