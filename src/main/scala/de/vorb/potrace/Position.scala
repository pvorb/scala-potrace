package de.vorb.potrace

sealed trait Position

object Position {
  case object Relative extends Position
  case object Absolute extends Position
}
