package launchpadutil.launchpad

import javax.sound.midi.ShortMessage

interface BasePad {
	val gridPosition: Byte
	val code: Byte
	val command: Int

	companion object {
		fun new(x: Int, y: Int, command: Int = ShortMessage.NOTE_ON): BasePad {
			return object : BasePad {
				override val code: Byte = codeFor(x, y)
				override val gridPosition: Byte =
					(if (command == ShortMessage.NOTE_ON) code + 9 else code - 48).toByte()
				override val command: Int = command
			}
		}

		/*@JvmStatic
		fun codeFor(x: Int, y: Int): Int {
			return (x shl 4) + y
		}*/
		@JvmStatic
		fun codeFor(x: Int, y: Int): Byte {
			return ((x shl 4) + y).toByte()
		}

		fun parseIndex(key: String): Pair<Int, Int> {
			val col = key.takeLastWhile { it.isDigit() }
			val row = key.take(key.length - col.length)

			val rowInt = if (row == "CC") { 0 } else { row[0].code - 64 }
			return rowInt to col.toInt()-1
		}
	}
}