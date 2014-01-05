package de.vorb.potrace

import java.awt.image.BufferedImage
import java.io.BufferedOutputStream
import scala.concurrent.{ Future, blocking }
import scala.concurrent.ExecutionContext
import javax.imageio.ImageIO
import java.io.File
import scala.concurrent.Await
import scala.concurrent.duration._
import java.io.FileOutputStream
import scala.concurrent.ExecutionContext.Implicits
import java.io.InputStreamReader
import java.io.BufferedReader

object Potrace {
  private def writeImageToPBM(img: BufferedImage, os: BufferedOutputStream)(
    implicit context: ExecutionContext): Future[Unit] = Future {
    blocking {
      var x = 0
      var y = 0

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

      os.flush()
      os.close()
    }
  }

  def apply(img: BufferedImage)(
    implicit context: ExecutionContext = ExecutionContext.Implicits.global) = {
    val pb = new ProcessBuilder("potrace", "--svg")
    val p = pb.start()

    writeImageToPBM(img, new BufferedOutputStream(p.getOutputStream))

    p.getInputStream
  }
}
