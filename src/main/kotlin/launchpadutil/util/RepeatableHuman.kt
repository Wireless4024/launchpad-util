package launchpadutil.util

import launchpadutil.api.Human

class RepeatableHuman(val human: Human, val times: Int) : ActionList()