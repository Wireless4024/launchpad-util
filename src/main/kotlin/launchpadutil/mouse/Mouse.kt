package launchpadutil.mouse

import launchpadutil.api.Human
import launchpadutil.robot.BetterRobot
import launchpadutil.util.LLinkedList

open class Mouse(val key: Int, modifiers: LLinkedList<Int>? = null) : Human(Companion.Type.Mouse, modifiers) {
	override suspend fun accept(robot: BetterRobot) {
		applyModifiers(robot)
		robot.mousePress(key)
		robot.waitForIdle()
		if (!robot.lowlatency) delay()
		robot.mouseRelease(key)
		robot.waitForIdle()
		releaseModifiers(robot)
		if (!robot.lowlatency) delay()
	}
}