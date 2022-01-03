package launchpadutil.launchpad

import launchpadutil.event.LaunchpadEvent
import javax.sound.midi.ShortMessage

interface GridEvent {
	fun register(pad: BasePad, task: LaunchpadEvent)
	fun unregister(pad: BasePad)
	fun hasEvent(pad: BasePad): Boolean
	suspend fun dispatch(msg: ShortMessage, launchpad: LaunchpadController)
}