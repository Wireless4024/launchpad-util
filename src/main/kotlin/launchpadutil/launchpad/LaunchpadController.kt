package launchpadutil.launchpad

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import launchpadutil.event.LaunchpadEvent
import launchpadutil.launchpad.mini.MiniPad
import launchpadutil.util.LaunchpadDevice
import java.io.FileInputStream
import javax.imageio.ImageIO
import javax.imageio.stream.ImageInputStream
import javax.sound.midi.ShortMessage

abstract class LaunchpadController(val launchpad: LaunchpadDevice, val channel: Int = 0) : AutoCloseable {
	open val transmitter get() = launchpad.transmitter
	open val receiver get() = launchpad.receiver

	abstract val pads: Iterator<BasePad>

	private val rcv: LaunchpadReceiver

	suspend fun repaint() {
		rcv.repaint()
	}

	suspend fun switchInstance(name: String) {
		rcv.switchInstance(name)
	}

	suspend fun register(pad: MiniPad, task: LaunchpadEvent?) {
		rcv.register(pad, task)
	}

	init {
		launchpad.open()
		rcv = LaunchpadReceiver(this)
		transmitter.receiver = rcv
		Runtime.getRuntime().addShutdownHook(Thread {
			this@LaunchpadController.close()
		})
	}

	suspend fun setColor(pad: BasePad, color: PadColor = PadColor.BLANK) {
		withContext(Dispatchers.IO) {
			receiver.send(ShortMessage(pad.command, channel, pad.code + 0, color.code), -1)
		}
	}

	abstract suspend fun setAvailable(pad: BasePad)
	abstract suspend fun setActive(pad: BasePad)

	suspend fun fillColor(color: PadColor = PadColor.BLANK) {
		for (pad in MiniPad.row(-1))
			setColor(pad, color)
	}

	abstract suspend fun drawString(string: String, delay: Long = 100L)
	abstract suspend fun drawGIF(path: ImageInputStream)
	suspend fun drawGIF(path: String) {
		withContext(Dispatchers.IO) {
			drawGIF(ImageIO.createImageInputStream(FileInputStream(path)))
		}
	}

	override fun close() {
		if (launchpad.isOpen) {
			runBlocking { fillColor() }
			launchpad.close()
		}
	}

	companion object {

	}
}