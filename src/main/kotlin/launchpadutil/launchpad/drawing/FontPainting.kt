package launchpadutil.launchpad.drawing

import launchpadutil.launchpad.LaunchpadController
import launchpadutil.launchpad.mini.MiniPad
import launchpadutil.launchpad.mini.MiniPadColor
import java.awt.*
import java.awt.image.BufferedImage
import kotlin.math.absoluteValue
import kotlin.math.max

class FontPainting(val gridSize: Int = 9) {
	private var image = BufferedImage(gridSize, gridSize, BufferedImage.TYPE_BYTE_GRAY)
	private val buf = IntArray(gridSize * gridSize)
	val width get() = image.width - gridSize

	init {
	}

	fun newImage(width: Int = 0) {
		image = BufferedImage(max(width, gridSize), gridSize, BufferedImage.TYPE_BYTE_GRAY)
	}

	fun drawString(str: String, font: String = "sans serif", fontSize: Int = 10) {
		val g = image.createGraphics()
		//  g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE)
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
		g.color = Color.BLACK
		g.fill(Rectangle(Dimension(image.width, gridSize)))
		g.color = Color.WHITE
		g.font = Font(font, Font.PLAIN, fontSize)// Font.decode("Arial-BOLD-18")
		val wid = g.fontMetrics.stringWidth(str) + (gridSize shl 1)// + gridSize
		if (str.length == 1 && image.width > gridSize) {
			newImage(gridSize)
			drawString(str, font, fontSize)
			return
		} else if ((str.length > 1 && (wid > image.width || image.width > wid))) {
			newImage(wid)
			drawString(str, font, fontSize)
			return
		}
		//println(g.fontMetrics.charWidth('B'))
		g.drawString(str, if (str.length == 1) 0 else 9, 9)
		//g.fillOval(0, 0, 9, 9)

		g.dispose()
	}

	fun shiftBuffer(right: Int) {
		if (right == 0) return
		System.arraycopy(buf, 0, buf, right.coerceAtLeast(0), buf.size - right.absoluteValue)
	}

	fun shiftUp(up: Int) {
		if (up == 0) return
		for (i in up until gridSize) {
			System.arraycopy(buf, i * gridSize, buf, (i - up) * gridSize, gridSize)
		}
		for (i in 1..up)
			for (j in 0 until 9)
				buf[((gridSize - i) * 9) + j] = 0
	}

	suspend fun paint(launchpad: LaunchpadController, offset: Int = 0, right: Int = 0, up: Int = 0) {
		val data = image.data
		val pix = data.getPixels(offset, 0, gridSize, gridSize, buf)
		shiftBuffer(right)
		shiftUp(up)
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
			launchpad.setColor(pad, mp[buf[if (idx > 7) idx + 1 else idx]])
		}
	}

	fun booleans(): BooleanArray {
		return image.data.getPixels(0, 0, gridSize, gridSize, buf).map { it == 1 }.toBooleanArray()
	}

	override fun toString(): String {
		val buf = image.data.getPixels(0, 0, image.width, gridSize, null as IntArray?)
		shiftBuffer(1)
		shiftUp(1)
		println(buf.contentToString())
		for (i in buf.indices)
			buf[i] = when (buf[i]) {
				in 204..255 -> 5
				in 153..203 -> 4
				in 102..152 -> 3
				in 102..152 -> 2
				in 51..101 -> 1
				else -> 0
			}
		return buf.asSequence().chunked(image.width)
			.joinToString("\n", transform = {
				it.joinToString("", "|", "|", transform = {
					when (it) {
						5 -> " # " // red
						4 -> " $ " // orange
						3 -> " O " // orange med
						2 -> " Y " // orange lig
						1 -> " I " // green
						else -> "   " // blank
					}
				})
			})
	}
}