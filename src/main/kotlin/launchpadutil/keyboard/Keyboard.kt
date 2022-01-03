package launchpadutil.keyboard

import launchpadutil.api.Human
import launchpadutil.robot.BetterRobot
import launchpadutil.util.LLinkedList
import java.awt.event.KeyEvent

abstract class Keyboard(val key: Int, modifiers: LLinkedList<Int>? = null) :
	Human(Human.Companion.Type.Keyboard, modifiers) {
	abstract override suspend fun accept(robot: BetterRobot)

	companion object {
		val SHIFT_DOWN = KeyDown(KeyEvent.VK_SHIFT)
		val SHIFT_UP = KeyUp(KeyEvent.VK_SHIFT)

		val CTRL_DOWN = KeyDown(KeyEvent.VK_CONTROL)
		val CTRL_UP = KeyUp(KeyEvent.VK_CONTROL)

		val ALT_DOWN = KeyDown(KeyEvent.VK_ALT)
		val ALT_UP = KeyUp(KeyEvent.VK_ALT)
	}
}