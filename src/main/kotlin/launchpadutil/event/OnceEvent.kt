package launchpadutil.event

import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import launchpadutil.coroutine.Scopes.EventScope
import launchpadutil.launchpad.LaunchpadController

/**
 * this event will fire when button pressed
 */
open class OnceEvent(override val task: suspend LaunchpadEvent.() -> Unit) : LaunchpadEvent {
	override suspend fun onPress(launchpad: LaunchpadController): Job {
		return EventScope.launch { task(this@OnceEvent) }
	}

	override suspend fun onPressSuccess(launchpad: LaunchpadController) {}
	override suspend fun onRelease(launchpad: LaunchpadController) {}
}