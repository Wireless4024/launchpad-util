package launchpadutil.launchpad

enum class LaunchpadChannel(val channel: Byte) {
	C1(0), C2(1), C3(2), C4(3),
	C5(4), C6(5), C7(6), C8(7),
	C9(8), C10(9), C11(10), C12(11),
	C13(12), C14(13), C15(14), C16(15);

	fun get(channel: Int): LaunchpadChannel {
		return when (channel) {
			0 -> C1
			1 -> C2
			2 -> C3
			3 -> C4
			4 -> C5
			5 -> C6
			6 -> C7
			7 -> C8
			8 -> C9
			9 -> C10
			10 -> C11
			11 -> C12
			12 -> C13
			13 -> C14
			14 -> C15
			15 -> C16
			else -> throw RuntimeException("someone hacking the universe")
		}
	}
}