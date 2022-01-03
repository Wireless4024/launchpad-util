package launchpadutil.error


open class LaunchpadException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)