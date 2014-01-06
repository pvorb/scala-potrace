package de.vorb.potrace

import scala.xml.Elem

class VectorGraph(val xml: Elem) {
  def width = {
    val VectorGraph.Points(w) = (xml \ "@width").toString
    w.toDouble
  }

  def height = {
    val VectorGraph.Points(h) = (xml \ "@height").toString
    h.toDouble
  }

  def paths: List[Path] = {
    val ps = xml \ "g" \ "path"
    ps.toList map { path =>
      Path(PathParsers.parse(PathParsers.path,
        (path \\ "@d").toString).get)
    }
  }
}

object VectorGraph {
  val Points = """(\d+\.\d+)pt""".r
}
