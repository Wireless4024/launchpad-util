package launchpadutil.mouse

import java.awt.event.MouseEvent

object LMB : Mouse(MouseEvent.BUTTON1_DOWN_MASK) {
	override fun toString() = "LMB"
}