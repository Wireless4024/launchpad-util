package launchpadutil.util

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine

class Promise<T>(val executor: suspend (ok: suspend (T) -> Unit, err: suspend (Throwable) -> Unit) -> T) {
	lateinit var continuation: CancellableContinuation<T>
	public var status = 0
	var value: T? = null
	var error: Throwable? = null

	private fun ok(v: T) {
		value = v
	}

	suspend fun await(): T? {
		if (status == 0) {
			status = 1
			suspendCancellableCoroutine<T> {
				continuation = it

				runBlocking(Dispatchers.Default) {
					executor(::ok, ::error)
				}
			}
			status = 2
		}

		return value
	}
}