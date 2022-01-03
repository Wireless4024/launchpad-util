package launchpadutil.launchpad.mini

import launchpadutil.launchpad.PadColor
import launchpadutil.util.LLinkedList

enum class MiniPadColor(override val code: Int) : PadColor {
	BLANK(0),
	RED_LIGHT(1),
	RED_MEDIUM(2),
	RED(3),

	GREEN(16),
	ORANGE_LIGHT(17),
	ORANGE_MEDIUM(18),
	ORANGE(19), ;

	companion object {
		val RAINBOW = LLinkedList(GREEN, ORANGE_LIGHT, RED_LIGHT, ORANGE_MEDIUM, RED_MEDIUM, ORANGE, RED)
		val INTENSITY = arrayOf(BLANK, GREEN, ORANGE_LIGHT, ORANGE_MEDIUM, ORANGE, RED)
	}
}