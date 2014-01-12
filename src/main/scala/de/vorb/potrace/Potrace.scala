package de.vorb.potrace

import java.awt.image.BufferedImage
import java.io.{ BufferedOutputStream, OutputStream }
import scala.Array.canBuildFrom
import scala.concurrent.{ ExecutionContext, Future, blocking }
import scala.xml.XML
import java.io.InputStreamReader
import javax.xml.parsers.SAXParserFactory

object Potrace {
  private lazy val factory = {
    val factory = SAXParserFactory.newInstance()
    factory.setValidating(false)
    factory.setFeature("http://xml.org/sax/features/validation", false)
    factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false)
    factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
    factory.setFeature("http://xml.org/sax/features/external-general-entities", false)
    factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false)
    factory
  }

  private def writeImageToPBM(img: BufferedImage, os: BufferedOutputStream)(
    implicit context: ExecutionContext): Future[Unit] = Future {

    var x = 0
    var y = 0

    blocking {
      val height = img.getHeight
      val width = img.getWidth

      os.write(s"P1\n$width $height\n".toCharArray.map(c => (c & 0xFF).toByte))

      while (y < height) {
        while (x < width) {
          if (x > 0)
            os.write(' ')

          if (img.getRGB(x, y) == 0xFFFFFFFF) {
            os.write('0')
          } else {
            os.write('1')
          }

          x += 1
        }

        os.write('\n')

        x = 0
        y += 1
      }

      os.close()
    }
  }

  def apply(img: BufferedImage)(
    implicit context: ExecutionContext = ExecutionContext.Implicits.global): Future[VectorGraph] = {
    require(img != null, "invalid image")

    val pb = new ProcessBuilder("potrace", "--svg")
    val p = pb.start()

    writeImageToPBM(img, new BufferedOutputStream(p.getOutputStream))

    Future {
      val doc = blocking {
        XML.withSAXParser(factory.newSAXParser).load(p.getInputStream)
      }

      new VectorGraph(doc)
    }
  }
}
