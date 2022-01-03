package launchpadutil.mouse

import launchpadutil.robot.BetterRobot
import launchpadutil.util.LLinkedList

class MouseDown(button: Int, modifiers: LLinkedList<Int>? = null) : Mouse(button, modifiers) {
	override suspend fun accept(robot: BetterRobot) {
		applyModifiers(robot)
		robot.mousePress(key)
		if (!robot.lowlatency) robot.waitForIdle()
	}
}