package launchpadutil.event

import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import launchpadutil.coroutine.Scopes.EventScope
import launchpadutil.launchpad.LaunchpadController

/**
 * this event will fire until it done it will continue if [HoldEvent.loop] is true, or [HoldEvent.onRelease] called and
 */
open class HoldEvent(private val loop: Boolean, override val task: suspend LaunchpadEvent.() -> Unit) : LaunchpadEvent {
	private var job: Job? = null
	var released: (suspend LaunchpadEvent.() -> Unit)? = null

	override suspend fun onPress(launchpad: LaunchpadController): Job {
		val job = EventScope.launch {
			if (loop) {
				while (isActive)
					task(this@HoldEvent)
			} else {
				task(this@HoldEvent)
			}
		}

		this.job = job

		return job
	}

	override suspend fun onPressSuccess(launchpad: LaunchpadController) {}

	override suspend fun onRelease(launchpad: LaunchpadController) {
		if (job != null) {
			job?.cancelAndJoin()
			job = null
			released?.invoke(this)
		}
	}
}