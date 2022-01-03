package launchpadutil.config

data class EventConfiguration(
	val type: String = "once",
	val fast: Boolean = false,
	val repeat: Boolean = false,
	val blocking: Boolean = true
) {
	fun toMap(): MutableMap<String, Any?> {
		val map = mutableMapOf<String, Any?>()
		map["type"] = type
		map["fast"] = fast
		map["repeat"] = repeat
		map["blocking"] = blocking
		return map
	}

	companion object {
		fun fromYml(yml: YamlFile): EventConfiguration {
			val cfg = yml.getSection("event") ?: return EventConfiguration()
			val type = cfg["type"] as? String ?: "once"
			val fast = cfg["fast"] as? Boolean ?: false
			val repeat = cfg["repeat"] as? Boolean ?: false
			val blocking = cfg["blocking"] as? Boolean ?: false

			return EventConfiguration(type, fast, repeat, blocking)
		}
	}
}