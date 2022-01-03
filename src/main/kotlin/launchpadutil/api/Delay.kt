package launchpadutil.api

import kotlinx.coroutines.delay
import launchpadutil.robot.BetterRobot

class Delay(private val ms: LongRange) : Human(Human.Companion.Type.Action) {
	constructor(token: String) : this(if (token.isEmpty()) ZERO else {
		val a = token.split("-", limit = 2)
		when (a.size) {
			2 -> (a[0].toLongOrNull() ?: 0L)..(a[1].toLongOrNull() ?: 0L)
			1 -> a.first().toLongOrNull()?.let { it..it } ?: ZERO
			else -> ZERO
		}
	})

	override suspend fun accept(robot: BetterRobot) {
		delay(ms.random())
	}

	override fun toString() = "Delay($ms)"

	companion object {
		val ZERO = 0L..0

		/**
		 * quarter human delay
		 */
		val QHD = 8L..12

		/**
		 * half human delay
		 */
		val HHD = 16L..20

		/**
		 * human delay
		 */
		val HD = 32L..48
	}
}