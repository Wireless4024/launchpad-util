package launchpadutil.keyboard

import launchpadutil.robot.BetterRobot
import launchpadutil.util.LLinkedList
import java.awt.event.KeyEvent
import java.awt.event.KeyEvent.getKeyText

open class KeyUp(key: Int, modifiers: LLinkedList<Int>? = null) : Keyboard(key, modifiers) {
	constructor(key: Char) : this(KeyEvent.getExtendedKeyCodeForChar(key.code))

	override suspend fun accept(robot: BetterRobot) {
		robot.keyRelease(key)
		robot.waitForIdle()
		releaseModifiers(robot)
		if (!robot.lowlatency) delay()
	}

	override fun toString() =
		"KeyUp(${modifiers?.joinToString("+", postfix = "+") { getKeyText(it) } ?: ""}${getKeyText(key)})"
}