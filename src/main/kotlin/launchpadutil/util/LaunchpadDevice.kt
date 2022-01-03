package launchpadutil.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiSystem

class LaunchpadDevice(val name: String, val receiverDev: MidiDevice, val transmitterDev: MidiDevice) : AutoCloseable {
	val receiver = receiverDev.receiver
	val transmitter = transmitterDev.transmitter

	fun open() {
		receiverDev.open()
		transmitterDev.open()
	}

	val isOpen get() = receiverDev.isOpen && transmitterDev.isOpen

	companion object {
		val PREFIX = "Launchpad"

		suspend fun scanDevices(): List<LaunchpadDevice> = withContext(Dispatchers.IO) {
			val dev = MidiSystem.getMidiDeviceInfo().asSequence().filter { it.description.startsWith(PREFIX) }
			val devices = dev.groupBy { "${it.description}(${it.name})" }
				.mapNotNullTo(ArrayList()) {
					it.value.run {
						if (size < 2) return@mapNotNullTo null

						val f = MidiSystem.getMidiDevice(first())
						val s = MidiSystem.getMidiDevice(elementAt(1))

						if (f.maxReceivers == -1) {
							return@mapNotNullTo LaunchpadDevice(it.key, f, s)
						} else {
							return@mapNotNullTo LaunchpadDevice(it.key, s, f)
						}
					}
				}
			return@withContext devices
		}
	}

	override fun toString(): String {
		return "$name | $receiver | $transmitter"
	}

	override fun close() {
		try {
			receiverDev.close()
			transmitterDev.close()
		} catch (e: Throwable) {
		}
	}
}