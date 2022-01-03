package launchpadutil.launchpad

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import launchpadutil.coroutine.Scopes.EventScope
import launchpadutil.event.LaunchpadEvent
import launchpadutil.launchpad.mini.MiniGridEvent
import launchpadutil.launchpad.mini.MiniPad
import javax.sound.midi.MidiMessage
import javax.sound.midi.Receiver
import javax.sound.midi.ShortMessage

class LaunchpadReceiver(val launchpad: LaunchpadController) : Receiver {

	val rcv = launchpad.receiver
	private val instances = HashMap<String, GridEvent>()
	private var instance: GridEvent = MiniGridEvent()

	suspend fun switchInstance(name: String) {
		var i = instances[name]
		if (i == null) {
			i = MiniGridEvent()
			instances[name] = i
		}

		instance = i

		launchpad.drawString(name.ifEmpty { "Global" }, 50)
		//repaint()
	}

	suspend fun register(pad: MiniPad, task: LaunchpadEvent?) {
		if (task != null) {
			instance.register(pad, task)
			launchpad.setAvailable(pad)
		} else {
			instance.unregister(pad)
			launchpad.setColor(pad)
		}
	}

	suspend fun repaint() {
		for (pad in launchpad.pads) {
			if (instance.hasEvent(pad)) {
				launchpad.setAvailable(pad)
			} else {
				launchpad.setColor(pad, PadColor.BLANK)
			}
			delay(1)
		}
	}

	init {
		instances[""] = instance

		// ensure 4 threads are active in coroutines
		GlobalScope.launch(Dispatchers.IO) {}
		GlobalScope.launch(Dispatchers.IO) {}
		GlobalScope.launch(Dispatchers.IO) {}
		GlobalScope.launch(Dispatchers.IO) {}
	}

	override fun close() = launchpad.close()

	override fun send(message: MidiMessage, timeStamp: Long) {
		if (message is ShortMessage) {
			EventScope.launch {
				instance.dispatch(message, launchpad)
			}
		}
		/*println(System.currentTimeMillis())
		println(message.javaClass)
		println(i++)
		if (message is ShortMessage) {
			println("channel " + message.channel)
			println("command " + message.command)
			println("status " + message.status)
			println("data1 " + message.data1)
			println("data2 " + message.data2)

			println(MiniPad(message.data1, message.command))

			with(message) {
				if (data1 == 104 && data2 == 127) {
					launchpad.receiver.send(ShortMessage(ShortMessage.NOTE_ON, 0, 104, j++), -1)

					println("K = " + (j - 1))
				}
				if (data1 == 120 && data2 == 127) {
					launchpad.receiver.send(ShortMessage(ShortMessage.NOTE_ON, 0, 120, k++), -1)

					println("J = " + (j - 1))
				}
				if (data1 == 120 && data2 == 0) {

				}
				if (data1 == 103 && data2 == 127) {
					launchpad.receiver.send(ShortMessage(ShortMessage.NOTE_ON, 0, 104, --k - 1), -1)

					println("K = $j")
				}
				if (data1 == 119 && data2 == 127) {
					launchpad.receiver.send(ShortMessage(ShortMessage.NOTE_ON, 0, 120, --j - 1), -1)

					println("J = $j")
				}
			}*/

		/*println()*/
	}
}