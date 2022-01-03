package launchpadutil.util

import kotlinx.coroutines.delay
import java.awt.Robot
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import kotlin.random.Random

/**
 * random delay between 16-20 ms avg=18ms
 */
suspend fun halfHumanKeyDelay() {
	delay(Random.nextLong(16, 20))
}

suspend fun quarterHumanKeyDelay() {
	delay(Random.nextLong(8, 12))
}

suspend fun Robot.mouse(key: Int) {
	mousePress(key)
	halfHumanKeyDelay()
	mouseRelease(key)
	halfHumanKeyDelay()
}

suspend fun Robot.leftMouse() {
	mouse(MouseEvent.BUTTON1_DOWN_MASK)
}

suspend fun Robot.press(key: Int) {
	keyPress(key)
	halfHumanKeyDelay()
	keyRelease(key)
	halfHumanKeyDelay()
}


suspend fun kd() {
	delay(Random.nextLong(25, 35))
}


suspend fun Robot.press(str: String) {
	for (i in str)
		press(KeyEvent.getExtendedKeyCodeForChar(i.code))
}
