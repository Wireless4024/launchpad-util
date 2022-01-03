package launchpadutil.config

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import launchpadutil.api.Human
import launchpadutil.event.BlockingEvent
import launchpadutil.event.HoldEvent
import launchpadutil.event.LaunchpadEvent
import launchpadutil.event.OnceEvent
import launchpadutil.robot.BetterRobot

data class HumanConfiguration(
	val key: String,
	val description: String,
	val expression: String,
	val cfg: EventConfiguration
) :
	Configuration {
	override suspend fun save(yml: YamlFile) {
		yml["name"] = key
		yml["description"] = description
		yml["expression"] = expression
		yml["event"] = cfg.toMap()
		withContext(Dispatchers.IO) { yml.save() }
	}

	override suspend fun createEvent(): LaunchpadEvent {
		val human = Human.parse(expression)
		val rb = BetterRobot(cfg.fast)
		val ev: suspend LaunchpadEvent.() -> Unit = { human.accept(rb) }

		val lpEvent = when (cfg.type) {
			"once" -> {
				OnceEvent(ev)
			}
			"hold" -> {
				HoldEvent(cfg.repeat, ev)
			}
			else -> throw UnsupportedOperationException()
		}

		return if (cfg.blocking) BlockingEvent(lpEvent) else lpEvent
	}

	companion object {
		fun load(yml: YamlFile): HumanConfiguration {
			var name = yml.getString("name")
			if (name.isEmpty()) name = yml.file.nameWithoutExtension
			return HumanConfiguration(
				name,
				yml.getString("description"),
				yml.getString("expression"),
				EventConfiguration.fromYml(yml)
			)
		}
	}
}