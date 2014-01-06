package de.vorb.potrace

import scala.util.parsing.combinator.RegexParsers

object PathParsers extends RegexParsers {
  override def skipWhitespace = false

  def space = """\s+""".r
  def optSpace = """\s*""".r
  def num: Parser[Double] = """-?\d+(\.\d*)?""".r ^^ { _.toDouble }

  def pos(cmd: Char): Parser[Position] =
    (s"[$cmd${cmd.toUpper}]\\s*").r ^^ { str =>
      if (str.apply(0).isLower)
        Position.Relative
      else
        Position.Absolute
    }

  def moveTo: Parser[MoveTo] =
    pos('m') ~ num ~ space ~ num ^^ {
      case pos ~ x ~ _ ~ y =>
        MoveTo(pos, x, y)
    }

  def lineTo: Parser[LineTo] =
    pos('l') ~ num ~ space ~ num ^^ {
      case pos ~ x ~ _ ~ y =>
        LineTo(pos, x, y)
    }

  def cubicBezier: Parser[CubicBezier] =
    (pos('c') ~ repsep(cubicComponent, space)) ^^ {
      case pos ~ comps => CubicBezier(pos, comps)
    }

  def cubicComponent: Parser[CubicComponent] =
    num ~ space ~ num ~ space ~ num ~ space ~ num ~ space ~ num ~ space ~ num ^^ {
      case x1 ~ _ ~ y1 ~ _ ~ x2 ~ _ ~ y2 ~ _ ~ x ~ _ ~ y =>
        CubicComponent(x1, y1, x2, y2, x, y)
    }

  def closePath: Parser[Command] =
    pos('z') ^^ { _ => ClosePath }

  def path: Parser[List[Command]] =
    repsep(moveTo | lineTo | cubicBezier | closePath, optSpace)
}
