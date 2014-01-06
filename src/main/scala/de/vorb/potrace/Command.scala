package de.vorb.potrace

sealed trait Command

case class MoveTo(pos: Position, x: Double, y: Double) extends Command

case class LineTo(pos: Position, x: Double, y: Double) extends Command

case class CubicBezier(pos: Position, points: List[CubicComponent]) extends Command

case class CubicComponent(x1: Double, y1: Double, x2: Double, y2: Double, x: Double, y: Double) extends Command

// TODO
case class CubicBezierSmooth() extends Command

case object ClosePath extends Command
