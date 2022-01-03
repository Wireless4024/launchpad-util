package launchpadutil.util

import kotlinx.coroutines.ensureActive
import launchpadutil.api.Delay
import launchpadutil.api.Human
import launchpadutil.keyboard.KeyDown
import launchpadutil.keyboard.KeyPress
import launchpadutil.keyboard.KeyUp
import launchpadutil.mouse.Mouse
import launchpadutil.mouse.MouseDown
import launchpadutil.mouse.MouseUp
import launchpadutil.robot.BetterRobot
import launchpadutil.robot.LowLatencyRobot
import kotlin.coroutines.coroutineContext

open class ActionList private constructor(private val list: LLinkedList<Human>) : Human(Companion.Type.List) {
	constructor() : this(LLinkedList<Human>())

	constructor(vararg args: Human) : this() {
		if (args.isNotEmpty())
			for (arg in args) {
				list.add(arg)
			}
	}

	fun add(human: Human): ActionList {
		val self = this.list
		if (human is ActionList) {
			val mod = human.modifiers
			if (human is RepeatableHuman) {
				if (human.times > 0) {
					val h = human.human
					h.insertModifiers(mod)
					for (i in 0 until human.times)
						self.add(h)
				}
			} else {
				val lst = human.list
				for (it in lst) {
					it.insertModifiers(mod)
					self.add(it)
				}
			}
		} else
			list.add(human)
		return this
	}

	fun setDelay(long: Long = 0): ActionList {
		return this.setDelay(long..long)
	}

	@Suppress("MemberVisibilityCanBePrivate")
	fun setDelay(range: LongRange): ActionList {
		for (human in list) {
			human.delayRange = range
		}
		return this
	}

	fun addAll(vararg humans: Human): ActionList {
		val self = this.list
		for (human in humans) {
			if (human is ActionList) {
				for (it in human.list) {
					it.insertModifiers(modifiers)
					self.add(it)
				}
			} else
				self.add(human)
		}
		return this
	}

	fun distinct(): ActionList {
		return ActionList(list.distinct())
	}

	override fun toPress(): ActionList {
		val new = ActionList()
		for (human in list) {
			val h = when (human) {
				is KeyPress -> KeyDown(human.key, human.modifiers)
				is KeyUp -> KeyDown(human.key, human.modifiers)
				is Mouse -> MouseDown(human.key)
				is MouseUp -> MouseDown(human.key)
				else -> human
			}
			h.delayRange = human.delayRange
			new.add(h)
		}
		return new
	}

	override fun toRelease(): ActionList {
		val new = ActionList()
		for (human in list) {
			val h = when (human) {
				is KeyPress -> KeyUp(human.key, human.modifiers)
				is KeyDown -> KeyUp(human.key, human.modifiers)
				is Mouse -> MouseUp(human.key, human.modifiers)
				is MouseDown -> MouseUp(human.key, human.modifiers)
				else -> human
			}
			h.delayRange = human.delayRange
			new.add(h)
		}
		return new
	}

	fun replaceLast(human: Human) {
		list.replaceLast(human)
	}

	fun addAll(list: ActionList): ActionList {
		val self = this.list
		val oth = list.list
		for (human in oth) {
			self.add(human)
		}
		return this
	}

	override suspend fun accept(robot: BetterRobot) {
		for (human in list) {
			coroutineContext.ensureActive()
			human.accept(robot)
		}
	}

	suspend fun accept(fast: Boolean = true) {
		if (fast) {
			setDelay()
			accept(LowLatencyRobot)
		} else {
			delayRange = Delay.HD
			accept(BetterRobot(false))
		}
	}

	override fun toString() = list.toString()
}