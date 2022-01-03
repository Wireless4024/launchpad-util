package launchpadutil.mouse

import launchpadutil.robot.BetterRobot

class MouseMove(val x: Int, val y: Int) : Mouse(0) {
	override suspend fun accept(robot: BetterRobot) {
		robot.mouseMove(x, y)
		if (!robot.lowlatency) delay()
	}

	override fun toString(): String {
		return "MouseMove($x, $y)"
	}
}