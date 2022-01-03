package launchpadutil.launchpad.mini

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import launchpadutil.coroutine.Scopes.EventScope
import launchpadutil.event.HoldEvent
import launchpadutil.event.LaunchpadEvent
import launchpadutil.launchpad.BasePad
import launchpadutil.launchpad.GridEvent
import launchpadutil.launchpad.LaunchpadController
import javax.sound.midi.ShortMessage

class MiniGridEvent : GridEvent {
	val size = 80

	val events = arrayOfNulls<LaunchpadEvent>(size)

	override fun hasEvent(pad: BasePad): Boolean {
		return events[pad.gridPosition + 0] != null
	}

	override fun register(pad: BasePad, task: LaunchpadEvent) {
		events[pad.gridPosition + 0] = task
	}

	override fun unregister(pad: BasePad) {
		events[pad.gridPosition + 0] = null
	}

	override suspend fun dispatch(msg: ShortMessage, launchpad: LaunchpadController) {
		val pad = MiniPad(msg) ?: return

		val event = events[pad.gridPosition + 0] ?: return

		if (msg.data2 != 0) {
			launchpad.setActive(pad)
			EventScope.launch(Dispatchers.IO) {
				event.onPress(launchpad)
				event.onPressSuccess(launchpad)
				if (event !is HoldEvent)
					launchpad.setAvailable(pad)
			}
		} else {
			event.onRelease(launchpad)
			if (event is HoldEvent)
				launchpad.setAvailable(pad)
		}
	}
}