package launchpadutil.robot

import java.awt.event.InputEvent

object LowLatencyRobot : BetterRobot(true) {

	private val tab = BooleanArray(Short.MAX_VALUE.toInt())
	private val mouse = BooleanArray(3)

	override fun keyPress(keycode: Int) {
		kp(keycode)
	}

	override fun kp(keycode: Int): Boolean {
		if (tab[keycode]) return false
		tab[keycode] = true
		super.keyPress(keycode)
		return true
	}

	override fun keyRelease(keycode: Int) {
		kr(keycode)
	}

	override fun kr(keycode: Int): Boolean {
		if (!tab[keycode]) return false
		tab[keycode] = false
		super.keyRelease(keycode)
		return true
	}

	override fun mousePress(buttons: Int) {
		val b1 = (buttons and InputEvent.BUTTON1_DOWN_MASK) == InputEvent.BUTTON1_DOWN_MASK
		val b2 = (buttons and InputEvent.BUTTON2_DOWN_MASK) == InputEvent.BUTTON2_DOWN_MASK
		val b3 = (buttons and InputEvent.BUTTON3_DOWN_MASK) == InputEvent.BUTTON3_DOWN_MASK
		var btn = 0
		if (b1 && !mouse[0]) btn = btn or InputEvent.BUTTON1_DOWN_MASK
		if (b2 && !mouse[1]) btn = btn or InputEvent.BUTTON2_DOWN_MASK
		if (b3 && !mouse[2]) btn = btn or InputEvent.BUTTON3_DOWN_MASK
		super.mousePress(btn)
	}

	override fun mouseRelease(buttons: Int) {
		val b1 = (buttons and InputEvent.BUTTON1_DOWN_MASK) == InputEvent.BUTTON1_DOWN_MASK
		val b2 = (buttons and InputEvent.BUTTON2_DOWN_MASK) == InputEvent.BUTTON2_DOWN_MASK
		val b3 = (buttons and InputEvent.BUTTON3_DOWN_MASK) == InputEvent.BUTTON3_DOWN_MASK
		var btn = 0
		if (b1 && mouse[0]) btn = btn or InputEvent.BUTTON1_DOWN_MASK
		if (b2 && mouse[1]) btn = btn or InputEvent.BUTTON2_DOWN_MASK
		if (b3 && mouse[2]) btn = btn or InputEvent.BUTTON3_DOWN_MASK
		super.mouseRelease(btn)
	}


}