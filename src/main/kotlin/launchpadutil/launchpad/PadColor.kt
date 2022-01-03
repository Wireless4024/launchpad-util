package launchpadutil.launchpad

interface PadColor {
	val code: Int

	companion object {
		val BLANK = object : PadColor {
			override val code: Int = 0
		}
	}
}