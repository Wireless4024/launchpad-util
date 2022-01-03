package launchpadutil.event

import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import launchpadutil.coroutine.Scopes.EventScope
import launchpadutil.launchpad.LaunchpadController

/**
 * this event can be fire one time and wait until complete to call again
 */
class BlockingEvent<T : LaunchpadEvent>(val inner: T) : OnceEvent(inner.task) {
	private var job: Job? = null
	override suspend fun onPress(launchpad: LaunchpadController): Job {
		var job = this.job
		if (job != null && !job.isCompleted) return EventScope.launch {}

		job = inner.onPress(launchpad)
		this.job = job
		return job
	}

	override suspend fun onPressSuccess(launchpad: LaunchpadController) {
		inner.onPressSuccess(launchpad)
	}

	override suspend fun onRelease(launchpad: LaunchpadController) {
		inner.onRelease(launchpad)
	}
}