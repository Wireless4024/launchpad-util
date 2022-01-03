package launchpadutil.robot

import launchpadutil.util.LinkedNode
import java.awt.Robot
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.concurrent.write

/**
 * this robot class will allow releasing key if event is cancelled
 */
open class BetterRobot(val lowlatency: Boolean = false) : Robot() {
	private val keyNodeHead = LinkedNode(0)
	private var keyNodeTail = keyNodeHead
	private val mouseNodeHead = LinkedNode(0)
	private var mouseNodeTail = mouseNodeHead
	private val lock = ReentrantLock()

	init {
		if (lowlatency) {
			super.setAutoDelay(0)
			super.setAutoWaitForIdle(false)
		}
	}

	open fun kp(keycode: Int): Boolean {
		keyPress(keycode)
		return true
	}

	override fun keyPress(keycode: Int) {
		if (keycode != 0)
			lock.withLock {
				keyNodeTail.next = LinkedNode(keycode)
			}
		super.keyPress(keycode)
	}

	protected fun rkp(keycode: Int) {
		super.keyPress(keycode)
	}

	open fun kr(keycode: Int): Boolean {
		keyRelease(keycode)
		return true
	}

	protected fun rkr(keycode: Int) {
		super.keyRelease(keycode)
	}

	private fun unlinkK(keycode: Int) {
		if (keycode == 0) return
		var n: LinkedNode<Int>? = keyNodeHead
		var p = n!!
		while (n != null) {
			if (n.value == keycode) {
				lock.withLock {
					if (n === keyNodeTail) {
						keyNodeTail = p
					} else
						p.next = n!!.next
				}
				return
			}
			p = n
			n = n.next
		}
	}

	private fun unlinkM(buttons: Int) {
		if (buttons == 0) return
		var n: LinkedNode<Int>? = mouseNodeHead
		var p = n!!
		while (n != null) {
			if (n.value == buttons) {
				lock.withLock {
					if (n === mouseNodeTail) {
						mouseNodeTail = p
					} else
						p.next = n!!.next
				}
				return
			}
			p = n
			n = n.next
		}
	}

	override fun keyRelease(keycode: Int) {
		super.keyRelease(keycode)
		unlinkK(keycode)
	}

	open fun keyReleaseAll() {
		lock.withLock {
			var n: LinkedNode<Int>? = keyNodeHead.next
			var p: LinkedNode<Int>
			while (n != null) {
				super.keyRelease(n.value)
				p = n
				n = n.next
				p.next = null
			}

			n = mouseNodeHead.next
			while (n != null) {
				super.mouseRelease(n.value)
				p = n
				n = n.next
				p.next = null
			}
		}
	}

	protected fun rmp(buttons: Int) {
		super.mousePress(buttons)
	}

	override fun mousePress(buttons: Int) {
		if (buttons != 0)
			mouseNodeHead.next = LinkedNode(buttons)
		super.mousePress(buttons)
	}

	protected fun rmr(buttons: Int) {
		super.mouseRelease(buttons)
	}

	override fun mouseRelease(buttons: Int) {
		super.mouseRelease(buttons)
		unlinkM(buttons)
	}
}