package launchpadutil.launchpad.drawing

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import launchpadutil.launchpad.LaunchpadController
import launchpadutil.launchpad.mini.MiniPad
import launchpadutil.launchpad.mini.MiniPadColor
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileInputStream
import javax.imageio.ImageIO
import javax.imageio.ImageReader
import kotlin.math.absoluteValue
import kotlin.math.max


class GifPainting(val gridSize: Int = 9) {
	private var image = BufferedImage(gridSize, gridSize, BufferedImage.TYPE_BYTE_GRAY)
	private val buf = IntArray(gridSize * gridSize)

	val width get() = image.width - gridSize
	val transform = AffineTransform.getScaleInstance(gridSize.toDouble(), gridSize.toDouble())

	init {

	}

	fun newImage(width: Int = 0) {
		image = BufferedImage(max(width, gridSize), gridSize, BufferedImage.TYPE_BYTE_GRAY)
	}

	fun splitGif(file: File?) {
		val reader: ImageReader = ImageIO.getImageReadersBySuffix("gif").next()
		reader.setInput(ImageIO.createImageInputStream(FileInputStream(file)), false)
		var outImage: BufferedImage? = null
		val lastImage = reader.read(0)
		var g: Graphics2D? = null
		for (i in 0 until reader.getNumImages(true)) {
			val image: BufferedImage = reader.read(i)
			if (g == null) {
				outImage = BufferedImage(
					image.width, image.height,
					BufferedImage.TYPE_4BYTE_ABGR
				)
				g = outImage.graphics as Graphics2D
			}
			g.drawImage(lastImage, 0, 0, null)
			ImageIO.write(outImage, "PNG", File("$i.png"))
		}
		g?.dispose()
	}

	@Suppress("BlockingMethodInNonBlockingContext")

	suspend fun drawGif(im: String, launchpad: LaunchpadController) {
		withContext(Dispatchers.IO) {
			val reader = ImageIO.getImageReadersBySuffix("gif").next()
			reader.setInput(ImageIO.createImageInputStream(FileInputStream(im)), false)
			val graphics = image.createGraphics()

			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
			graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)

			for (i in 0 until (reader.getNumImages(true))) {
				val image: BufferedImage = reader.read(i)
				graphics.drawImage(image, 0, 0, gridSize, gridSize, 0, 0, image.width, image.height, null)
				/* println(toString())
				 println(
					 this.image.raster.getPixels(0, 0, 9, 9, null as IntArray?).asSequence().chunked(75)
						 .map { it.toString() }.joinToString("\n")
				 )*/
				paint(launchpad)
				delay(100)
			}
			reader.dispose()
			graphics.dispose()
		}
	}

	fun shiftBuffer(right: Int) {
		System.arraycopy(buf, 0, buf, right.coerceAtLeast(0), buf.size - right.absoluteValue)
	}

	fun shiftUp(up: Int) {
		for (i in up until gridSize) {
			System.arraycopy(buf, i * gridSize, buf, (i - up) * gridSize, gridSize)
		}
		for (i in 1..up)
			for (j in 0 until 9)
				buf[((gridSize - i) * 9) + j] = 0
	}

	suspend fun paint(launchpad: LaunchpadController) {
		val data = image.data
		val pix = data.getPixels(0, 0, gridSize, gridSize, buf)
		//shiftBuffer(1)
		//shiftUp(1)
		for (i in buf.indices)
			buf[i] = when (buf[i]) {
				in 204..255 -> 5
				in 153..203 -> 4
				in 102..152 -> 3
				in 102..152 -> 2
				in 51..101 -> 1
				else -> 0
			}
		val mp = MiniPadColor.INTENSITY
		for ((idx, pad) in (MiniPad.row(-1) as Iterator<MiniPad>).withIndex()) {
			launchpad.setColor(pad, mp[buf[idx]])
		}
	}

	fun booleans(): BooleanArray {
		return image.data.getPixels(0, 0, gridSize, gridSize, buf).map { it == 1 }.toBooleanArray()
	}

	override fun toString(): String {
		image.data.getPixels(0, 0, gridSize, gridSize, buf)
		println(buf.asSequence().chunked(gridSize).map { it.joinToString { "% 4d".format(it) } }.joinToString("\n"))
		for (i in buf.indices)
			buf[i] = when (buf[i]) {
				in 204..255 -> 5
				in 153..203 -> 4
				in 102..152 -> 3
				in 102..152 -> 2
				in 51..101 -> 1
				else -> 0
			}
		return buf.asSequence().chunked(gridSize)
			.joinToString("\n", transform = {
				it.joinToString("", "|", "|", transform = {
					when (it) {
						5 -> " # " // red
						4 -> " $ " // orange
						3 -> " O " // orange med
						2 -> " Y " // ortange lig
						1 -> " I " // green
						else -> "   " // blank
					}
				})
			})
	}
}