import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.Robot
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Line
import javax.sound.sampled.Mixer
import javax.sound.sampled.TargetDataLine


object Util {
	suspend fun exec(cmd: String): String? {
		try {
			return withContext(Dispatchers.IO) {
				var process: Process? = null
				try {
					if (File("/usr/bin/bash").isFile) {
						process = Runtime.getRuntime().exec(arrayOf("/usr/bin/bash", "-c", cmd))
					} else if (File("/usr/bin/sh").isFile)
						process = Runtime.getRuntime().exec(arrayOf("/usr/bin/sh", "-c", cmd))

					if (process == null)
						throw IOException("failed to find cli")
					process.waitFor(10, TimeUnit.SECONDS)

					val out = withContext(Dispatchers.IO) { process.inputStream.readAllBytes() }
						.toString(Charsets.UTF_8)
					out.ifBlank {
						withContext(Dispatchers.IO) {
							process.errorStream.readAllBytes()
						}.toString(Charsets.UTF_8)
					}.trimIndent()
				} catch (ioe: Exception) {
					ioe.printStackTrace()
					null
				} finally {
					process?.destroyForcibly()
				}
			}
		} catch (e: Exception) {
			e.printStackTrace()
			return null
		}
	}

	fun swap(arr: Array<IntArray>, a: Int, b: Int, c: Int, d: Int) {
		val aa = arr[a]
		val ac = arr[c]
		val temp = aa[b]
		aa[b] = ac[d]
		ac[d] = temp
	}

	fun devices() = filterDevices(Line.Info(TargetDataLine::class.java))
	private fun filterDevices(supportedLine: Line.Info): List<Mixer.Info> {
		val result: MutableList<Mixer.Info> = mutableListOf()
		val infos = AudioSystem.getMixerInfo()
		for (info in infos) {
			val mixer = AudioSystem.getMixer(info)
			if (mixer.isLineSupported(supportedLine)) {
				result.add(info)
			}
		}
		return result
	}

	fun Robot.await() {
		try {
			waitForIdle()
		} catch (e: Throwable) {
		}
	}
}