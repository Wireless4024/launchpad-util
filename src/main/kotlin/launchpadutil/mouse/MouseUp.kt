package launchpadutil.mouse

import launchpadutil.robot.BetterRobot
import launchpadutil.util.LLinkedList

class MouseUp(button: Int, modifiers: LLinkedList<Int>? = null) : Mouse(button, modifiers) {
	override suspend fun accept(robot: BetterRobot) {
		releaseModifiers(robot)
		robot.mouseRelease(key)
		if (!robot.lowlatency) robot.waitForIdle()
	}
}