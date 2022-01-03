package launchpadutil.launchpad.mini

import launchpadutil.launchpad.BasePad
import launchpadutil.util.Iterble
import launchpadutil.util.RangedIter
import javax.sound.midi.ShortMessage
import launchpadutil.launchpad.BasePad.Companion.codeFor as C
import javax.sound.midi.ShortMessage.CONTROL_CHANGE as CC
import javax.sound.midi.ShortMessage.NOTE_ON as NO

enum class MiniPad(
	override val code: Byte,
	override val command: Int,
	override val gridPosition: Byte
) : BasePad {
	CC1(C(6, 8), CC, 0),
	CC2(C(6, 9), CC, 1),
	CC3(C(6, 10), CC, 2),
	CC4(C(6, 11), CC, 3),
	CC5(C(6, 12), CC, 4),
	CC6(C(6, 13), CC, 5),
	CC7(C(6, 14), CC, 6),
	CC8(C(6, 15), CC, 7),

	A1(C(0, 0), NO, 8),
	A2(C(0, 1), NO, 9),
	A3(C(0, 2), NO, 10),
	A4(C(0, 3), NO, 11),
	A5(C(0, 4), NO, 12),
	A6(C(0, 5), NO, 13),
	A7(C(0, 6), NO, 14),
	A8(C(0, 7), NO, 15),
	A(C(0, 8), NO, 16),

	B1(C(1, 0), NO, 17),
	B2(C(1, 1), NO, 18),
	B3(C(1, 2), NO, 19),
	B4(C(1, 3), NO, 20),
	B5(C(1, 4), NO, 21),
	B6(C(1, 5), NO, 22),
	B7(C(1, 6), NO, 23),
	B8(C(1, 7), NO, 24),
	B(C(1, 8), NO, 25),

	C1(C(2, 0), NO, 26),
	C2(C(2, 1), NO, 27),
	C3(C(2, 2), NO, 28),
	C4(C(2, 3), NO, 29),
	C5(C(2, 4), NO, 30),
	C6(C(2, 5), NO, 31),
	C7(C(2, 6), NO, 32),
	C8(C(2, 7), NO, 33),
	C(C(2, 8), NO, 34),

	D1(C(3, 0), NO, 35),
	D2(C(3, 1), NO, 36),
	D3(C(3, 2), NO, 37),
	D4(C(3, 3), NO, 38),
	D5(C(3, 4), NO, 39),
	D6(C(3, 5), NO, 40),
	D7(C(3, 6), NO, 41),
	D8(C(3, 7), NO, 42),
	D(C(3, 8), NO, 43),

	E1(C(4, 0), NO, 44),
	E2(C(4, 1), NO, 45),
	E3(C(4, 2), NO, 46),
	E4(C(4, 3), NO, 47),
	E5(C(4, 4), NO, 48),
	E6(C(4, 5), NO, 49),
	E7(C(4, 6), NO, 50),
	E8(C(4, 7), NO, 51),
	E(C(4, 8), NO, 52),

	F1(C(5, 0), NO, 53),
	F2(C(5, 1), NO, 54),
	F3(C(5, 2), NO, 55),
	F4(C(5, 3), NO, 56),
	F5(C(5, 4), NO, 57),
	F6(C(5, 5), NO, 58),
	F7(C(5, 6), NO, 59),
	F8(C(5, 7), NO, 60),
	F(C(5, 8), NO, 61),

	G1(C(6, 0), NO, 62),
	G2(C(6, 1), NO, 63),
	G3(C(6, 2), NO, 64),
	G4(C(6, 3), NO, 65),
	G5(C(6, 4), NO, 66),
	G6(C(6, 5), NO, 67),
	G7(C(6, 6), NO, 68),
	G8(C(6, 7), NO, 69),
	G(C(6, 8), NO, 70),

	H1(C(7, 0), NO, 71),
	H2(C(7, 1), NO, 72),
	H3(C(7, 2), NO, 73),
	H4(C(7, 3), NO, 74),
	H5(C(7, 4), NO, 75),
	H6(C(7, 5), NO, 76),
	H7(C(7, 6), NO, 77),
	H8(C(7, 7), NO, 78),
	H(C(7, 8), NO, 79),
	;

	companion object {
		// enum values constant faster than call values() method
		val values = values()

		private val rawGrid: Array<MiniPad>

		init {
			val grid = arrayOfNulls<MiniPad>(80)
			for (pad in values) {
				grid[pad.gridPosition.toInt()] = pad
			}
			@Suppress("UNCHECKED_CAST") // this is fine
			rawGrid = grid as Array<MiniPad>
		}

		fun row(row: Int): RangedIter<MiniPad> {
			return if (row == -1) RangedIter(rawGrid) else RangedIter(rawGrid, row * 9, if (row == 0) 8 else 9)
		}

		/*fun mktab() {
			for (c in 'A'..'H') {
				for (i in 1..9) {
					val row = c.code - 'A'.code
					println("""$c${if (i == 9) "" else i}(C($row, ${i - 1}), NO, ${i + 7 + (row * 9)}),""")
				}
				println("")
			}
		}*/


		operator fun get(ordinal: Int): MiniPad {
			return rawGrid[ordinal]
		}

		operator fun get(key: String): MiniPad {
			val (row, col) = BasePad.parseIndex(key)
			return get((row * 9) + col)
		}

		operator fun get(x: Int, y: Int): MiniPad {
			return rawGrid[(x * 9) + y + 8]
		}

		operator fun invoke(message: ShortMessage) = invoke(message.data1, message.command)

		operator fun invoke(data1: Int, command: Int): MiniPad? {
			return when (command) {
				ShortMessage.NOTE_ON -> get(data1 shr 4, data1 and 15)
				ShortMessage.CONTROL_CHANGE -> get(data1 and 7)
				else -> null
			}
		}

		internal class PadIterator(val arr: Array<MiniPad>) : Iterble<MiniPad> {
			private var cursor = 0
			private val end = arr.size

			override fun hasNext() = cursor < end

			override fun next(): MiniPad {
				if (cursor == 8) {
					cursor += 2
					return arr[9]
				}
				return arr[cursor++]
			}

			fun withIndex(): Iterble<Pair<Int, MiniPad>> {
				return object : Iterble<Pair<Int, MiniPad>> {
					override fun iterator() = this

					override fun hasNext() = this@PadIterator.hasNext()

					override fun next() = (cursor) to this@PadIterator.next()
				}
			}

			override fun iterator() = this
		}
	}
}