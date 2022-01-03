package launchpadutil.config

import launchpadutil.event.LaunchpadEvent
import java.util.*

interface Configuration {
	suspend fun save(yml: YamlFile)
	suspend fun createEvent(): LaunchpadEvent

	companion object {
		fun parse(yml: YamlFile): Configuration {
			return when (yml.getString("type").lowercase(Locale.getDefault())) {
				"human" -> HumanConfiguration.load(yml)
				else -> null
			} ?: object : Configuration {

				override suspend fun save(yml: YamlFile) {
				}

				override suspend fun createEvent(): LaunchpadEvent {
					TODO("Not yet implemented")
				}
			}
		}
	}
}