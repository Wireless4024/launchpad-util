package launchpadutil.event

import launchpadutil.api.Human
import launchpadutil.config.YamlFile
import launchpadutil.launchpad.LaunchpadController
import launchpadutil.robot.BetterRobot

class KeyHoldEvent(loop: Boolean, val robot: BetterRobot, task: suspend LaunchpadEvent.() -> Unit) :
	HoldEvent(loop, task) {
	override suspend fun onRelease(launchpad: LaunchpadController) {
		super.onRelease(launchpad)
		robot.keyReleaseAll()
	}


	companion object {
		fun parse(yml: YamlFile): KeyHoldEvent {
			return KeyHoldEvent(yml.getBoolean("event.loop"), BetterRobot()) {
				Human.parse(yml.getString("expression"))
			}
		}
	}
}