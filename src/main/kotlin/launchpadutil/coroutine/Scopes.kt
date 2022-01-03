package launchpadutil.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

object Scopes {
	val EventScope = CoroutineScope(Dispatchers.IO)
}