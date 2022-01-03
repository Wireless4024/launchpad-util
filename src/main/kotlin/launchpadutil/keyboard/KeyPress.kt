package launchpadutil.keyboard

import launchpadutil.robot.BetterRobot
import launchpadutil.util.LLinkedList
import java.awt.event.KeyEvent
import java.awt.event.KeyEvent.getKeyText

open class KeyPress(key: Int, val delay: LongRange = 16L..20, modifiers: LLinkedList<Int>? = null) :
	Keyboard(key, modifiers) {
	constructor(key: Char) : this(KeyEvent.getExtendedKeyCodeForChar(key.code))

	override suspend fun accept(robot: BetterRobot) {
		applyModifiers(robot)
		robot.keyPress(key)
		//lkd()
		// if use delay it will cancel and cause ghost key // can handle it now
		if (!robot.lowlatency) delay()
		robot.keyRelease(key)
		releaseModifiers(robot)
		if (!robot.lowlatency) delay()
	}

	override fun toString() =
		"KeyPress(${modifiers?.joinToString("+", postfix = "+") { getKeyText(it) } ?: ""}${getKeyText(key)})"
}