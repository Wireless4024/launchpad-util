package launchpadutil.keyboard

import launchpadutil.robot.BetterRobot
import launchpadutil.util.LLinkedList
import java.awt.event.KeyEvent
import java.awt.event.KeyEvent.getKeyText

open class KeyDown(key: Int, modifiers: LLinkedList<Int>? = null) : Keyboard(key, modifiers) {
	constructor(key: Char) : this(KeyEvent.getExtendedKeyCodeForChar(key.code))

	override suspend fun accept(robot: BetterRobot) {
		applyModifiers(robot)
		robot.keyPress(key)
		robot.waitForIdle()
		if (!robot.lowlatency) delay()
		// releaseModifiers(robot)
	}

	override fun toString() =
		"KeyDown(${modifiers?.joinToString("+", postfix = "+") { getKeyText(it) } ?: ""}${getKeyText(key)})"
}