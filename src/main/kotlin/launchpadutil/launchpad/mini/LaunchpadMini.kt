package launchpadutil.launchpad.mini

import kotlinx.coroutines.*
import launchpadutil.config.HumanConfiguration
import launchpadutil.config.YamlFile
import launchpadutil.launchpad.BasePad
import launchpadutil.launchpad.LaunchpadController
import launchpadutil.launchpad.drawing.FontPainting
import launchpadutil.util.LaunchpadDevice
import java.io.File
import javax.imageio.stream.ImageInputStream

class LaunchpadMini(launchpad: LaunchpadDevice) : LaunchpadController(launchpad) {
	override val pads: Iterator<MiniPad> get() = MiniPad.row(-1)
	private val STR_RENDERER = FontPainting()
	private var rendering: Job? = null


	override suspend fun setAvailable(pad: BasePad) {
		setColor(pad, MiniPadColor.GREEN)
	}

	override suspend fun setActive(pad: BasePad) {
		setColor(pad, MiniPadColor.ORANGE_LIGHT)
	}

	override suspend fun drawString(string: String, delay: Long) {
		rendering?.cancel()
		@Suppress("EXPERIMENTAL_API_USAGE")
		rendering = GlobalScope.launch {
			STR_RENDERER.drawString(string)
			if (STR_RENDERER.width == 0) {
				STR_RENDERER.shiftBuffer(1)
				STR_RENDERER.paint(this@LaunchpadMini, right = 1)
				delay(delay * 10)
			} else
				for (off in 0..STR_RENDERER.width) {
					STR_RENDERER.paint(this@LaunchpadMini, off)
					delay(delay)
				}
			this@LaunchpadMini.repaint()
		}
	}

	override suspend fun drawGIF(path: ImageInputStream) {
		TODO("Not yet implemented")
	}

	suspend fun loadDir(dir: File) {
		val acceptedExt = arrayOf("yml", "yaml", "config")
		val files = withContext(Dispatchers.IO) {
			dir.listFiles()
		}
		val cfgs = files?.asSequence()
			?.filter { acceptedExt.contains(it.extension) }
			?.map { YamlFile(it) }
			?.map {
				val load = HumanConfiguration.load(it) ?: return@map null
				load.key to load
			}

		cfgs?.forEach {
			println(it?.second)
			if (it != null)
				register(MiniPad[it.first], it.second.createEvent())
		}
	}

	suspend fun loadDir(dir: String) {
		loadDir(File(dir))
	}
}