package launchpadutil.event

import kotlinx.coroutines.Job
import launchpadutil.launchpad.LaunchpadController

interface LaunchpadEvent {
	val blocking: Boolean get() = true
	val task: suspend LaunchpadEvent.() -> Unit
	suspend fun onPress(launchpad: LaunchpadController): Job
	suspend fun onPressSuccess(launchpad: LaunchpadController)
	suspend fun onRelease(launchpad: LaunchpadController)
}