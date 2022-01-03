package launchpadutil.mouse

import java.awt.event.MouseEvent

object RMB : Mouse(MouseEvent.BUTTON3_DOWN_MASK) {
	// what heck?
	override fun toString() = "RMB"
}