package launchpadutil.api

import kotlinx.coroutines.delay
import launchpadutil.error.UnclosedBracketException
import launchpadutil.keyboard.KeyDown
import launchpadutil.keyboard.KeyPress
import launchpadutil.keyboard.KeyUp
import launchpadutil.keyboard.Keyboard
import launchpadutil.mouse.*
import launchpadutil.robot.BetterRobot
import launchpadutil.util.ActionList
import launchpadutil.util.LLinkedList
import launchpadutil.util.quarterHumanKeyDelay
import java.awt.Toolkit.getDefaultToolkit
import java.awt.datatransfer.DataFlavor
import java.awt.event.KeyEvent.*
import java.util.*

abstract class Human(val type: Type, var modifiers: LLinkedList<Int>? = null) {
	abstract suspend fun accept(robot: BetterRobot)
	var delayRange = 16L..20

	suspend fun applyModifiers(robot: BetterRobot) {
		val modifiers = this.modifiers
		if (modifiers?.isNotEmpty() == true) {
			for (mod in modifiers) {
				if (robot.kp(mod) && !robot.lowlatency)
					quarterHumanKeyDelay()
			}
		}
	}

	suspend fun releaseModifiers(robot: BetterRobot) {
		val modifiers = this.modifiers
		if (modifiers?.isNotEmpty() == true) {
			for (mod in modifiers) {
				if (robot.kr(mod) && !robot.lowlatency)
					quarterHumanKeyDelay()
			}
		}
	}

	fun mergeModifiers(other: LLinkedList<Int>?) {
		if (other != null) {
			val mod = modifiers
			if (mod == null) {
				modifiers = other
				return
			}
			for (it in other) {
				mod.add(it)
			}
		}
	}

	fun insertModifiers(other: LLinkedList<Int>?) {
		val mod = modifiers
		if (other != null) {
			if (mod == null) {
				modifiers = other
				return
			}
			val new = LLinkedList<Int>()
			for (it in other) {
				new.add(it)
			}
			for (it in mod) {
				new.add(it)
			}
			modifiers = new.distinct()
		}
	}

	open fun toPress(): Human {
		val h = when (this) {
			is KeyPress -> KeyDown(key, modifiers)
			is KeyUp -> KeyDown(key, modifiers)
			is MouseUp -> MouseDown(key)
			is Mouse -> MouseDown(key)
			else -> this
		}
		h.delayRange = delayRange
		return h
	}

	open fun toRelease(): Human {
		val h = when (this) {
			is KeyPress -> KeyUp(key, modifiers)
			is KeyDown -> KeyUp(key, modifiers)
			is MouseDown -> MouseUp(key, modifiers)
			is Mouse -> MouseUp(key, modifiers)
			else -> this
		}
		h.delayRange = delayRange
		return h
	}

	suspend fun delay() {
		with(delayRange) {
			if (last != start || start != 0L)
				delay(delayRange.random())
		}
	}

	companion object {
		enum class Type {
			Mouse,
			Keyboard,
			Action,
			List,
			JS,
			KTS,
		}

		val Char.rawCode get() = getExtendedKeyCodeForChar(this.code)

		private fun mod(modifiers: LLinkedList<Int>?, modifier: Int): LLinkedList<Int> {
			return modifiers?.apply { add(modifier) } ?: LLinkedList(modifier)
		}

		fun parse(str: String): ActionList {
			var i = 0
			val len = str.length
			val tokenList = ActionList()
			if (len == 0) return tokenList
			var anchor = 0
			var modifiers: LLinkedList<Int>? = null
			var repeat = 0
			while (i < len) {
				var char = str[i]

				if (char != '{' || (i > 0 && str[i - 1] == '\\')) {
					val token = when (char) {
						'%' -> KeyPress('5'.rawCode, modifiers = mod(modifiers, VK_SHIFT))
						'#' -> KeyPress('7'.rawCode, modifiers = mod(modifiers, VK_SHIFT))
						'?' -> KeyPress('/'.rawCode, modifiers = mod(modifiers, VK_SHIFT))
						'<' -> KeyPress(','.rawCode, modifiers = mod(modifiers, VK_SHIFT))
						'~', '!', '@', '$', '^', '&', '*', '(', ')', '_', '+', '|', '{', '}', ':', '"', '>'
						-> KeyPress(char.rawCode, modifiers = mod(modifiers, VK_SHIFT))
						else -> KeyPress(
							char.rawCode,
							modifiers = if (char.isUpperCase()) mod(modifiers, VK_SHIFT) else modifiers
						)
					}
					if (char == '{' && i > 0 && str[i - 1] == '\\') {
						if (repeat == 0)
							tokenList.replaceLast(token)
						else {
							tokenList.replaceLast(token)
							while (--repeat >= 1) tokenList.add(token)
							if (repeat < 0) repeat = 0
						}
					} else {
						if (repeat == 0)
							tokenList.add(token)
						else {
							while (--repeat >= 0) tokenList.add(token)
							if (repeat < 0) repeat = 0
						}
					}
					++i
					modifiers = null
					continue
				}
				++i
				if (str[i] == '}') continue
				anchor = i
				var sep = 0
				var skip = 0
				while (i < len && (((char != '}' || (char == '}' && i > 0 && str[i - 1] == '\\') || skip >= 0)))) {
					char = str[i++]
					when (char) {
						' ' -> if (sep == 0) sep = i
						'{' -> ++skip
						'}' -> --skip
					}
				}
				if (str[i - 1] != '}') throw UnclosedBracketException()

				var word = str.substring(anchor, (if (sep > 0) sep else i) - 1).lowercase(Locale.getDefault())
				if (word.isEmpty()) continue


				val args = if (sep == 0) "" else str.substring(sep, i - 1)
				val b = when (word) {
					"shift" -> modifiers?.add(VK_SHIFT) ?: run { modifiers = LLinkedList(VK_SHIFT) }
					"ctrl", "control" -> modifiers?.add(VK_CONTROL) ?: run { modifiers = LLinkedList(VK_CONTROL) }
					"alt" -> modifiers?.add(VK_ALT) ?: run { modifiers = LLinkedList(VK_ALT) }
					else -> null
				}
				if (b != null) {
					continue
				}
				val wasMod = word[0] == '!'
				if (wasMod) {
					word = word.substring(1)
				}
				val prefix = when (word[0]) {
					'+' -> '+'
					'-' -> '-'
					else -> null
				}.let {
					if (it == null)
						' '
					else {
						word = word.substring(1)
						it
					}
				}

				when (word) {
					"backspace", "bs" -> KeyPress(VK_BACK_SPACE)
					"cap", "capslock" -> KeyPress(VK_CAPS_LOCK)
					"context" -> KeyPress(VK_CONTEXT_MENU)
					"del", "delete" -> KeyPress(VK_DELETE)

					"delay" -> Delay(args)
					"hd" -> Delay(Delay.HD)
					"hhd" -> Delay(Delay.HHD)
					"qhd" -> Delay(Delay.QHD)

					"down" -> KeyPress(VK_DOWN)
					"end" -> KeyPress(VK_END)
					"enter", "return" -> KeyPress(VK_ENTER)
					"esc", "escape" -> KeyPress(VK_ESCAPE)
					"home" -> KeyPress(VK_HOME)
					"ins", "insert" -> KeyPress(VK_INSERT)
					"left" -> KeyPress(VK_LEFT)
					"lmb" -> LMB
					"num", "numlock" -> KeyPress(VK_NUM_LOCK)
					"pause", "break" -> KeyPress(VK_PAUSE)
					"pgdn", "pagedown" -> KeyPress(VK_PAGE_DOWN)
					"pgup", "pageup" -> KeyPress(VK_PAGE_UP)
					"print" -> KeyPress(VK_PRINTSCREEN)
					"right" -> KeyPress(VK_RIGHT)
					"rmb" -> RMB
					"scrolllock", "scroll-lock" -> KeyPress(VK_SCROLL_LOCK)
					"tab" -> KeyPress(VK_TAB)
					"up" -> KeyPress(VK_UP)
					"win", "windows", "start", "super" -> KeyPress(VK_WINDOWS)

					"expr" -> parse(args)
					"press" -> parse(args).toPress()
					"release" -> parse(args).toRelease()
					"repeat" -> {
						repeat = args.toIntOrNull() ?: 1
						null
					}
					"clipboard" -> parse(
						(getDefaultToolkit().systemClipboard.getData(DataFlavor.stringFlavor) as? String)
							?.replace("{", "\\{")
							?: ""
					)

					"shift" -> KeyPress(VK_SHIFT)
					"ctrl", "control" -> KeyPress(VK_CONTROL)
					"alt" -> KeyPress(VK_ALT)

					"f1" -> KeyPress(VK_F1)
					"f2" -> KeyPress(VK_F2)
					"f3" -> KeyPress(VK_F3)
					"f4" -> KeyPress(VK_F4)
					"f5" -> KeyPress(VK_F5)
					"f6" -> KeyPress(VK_F6)
					"f7" -> KeyPress(VK_F7)
					"f8" -> KeyPress(VK_F8)
					"f9" -> KeyPress(VK_F9)
					"f10" -> KeyPress(VK_F10)
					"f11" -> KeyPress(VK_F11)
					"f12" -> KeyPress(VK_F12)
					"f13" -> KeyPress(VK_F13)
					"f14" -> KeyPress(VK_F14)
					"f15" -> KeyPress(VK_F15)
					"f16" -> KeyPress(VK_F16)
					"f17" -> KeyPress(VK_F17)
					"f18" -> KeyPress(VK_F18)
					"f19" -> KeyPress(VK_F19)
					"f20" -> KeyPress(VK_F20)
					"f21" -> KeyPress(VK_F21)
					"f22" -> KeyPress(VK_F22)
					"f23" -> KeyPress(VK_F23)
					"f24" -> KeyPress(VK_F24)

					else -> null
				}?.let { hu ->
					val it = when (prefix) {
						'+' -> hu.toPress()
						'-' -> hu.toRelease()
						else -> hu
					}
					if (wasMod) {
						if (it is Keyboard)
							modifiers?.add(it.key) ?: run { modifiers = LLinkedList(it.key) }
					} else {
						it.modifiers = modifiers
						modifiers = null
						if (repeat == 0)
							tokenList.add(it)
						else {
							while (--repeat >= 0) tokenList.add(it)
							if (repeat < 0) repeat = 0
						}
					}
					Unit
				}
			}
			return tokenList
		}
	}
}

fun main() {
	for (i in 1..24)
		println(""""f$i"->KeyPress(VK_F$i)""")
}
