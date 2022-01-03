package launchpadutil.util

import launchpadutil.error.LaunchpadException

internal class Iter<T>(private var head: LinkedNode<T>?) : Iterable<T>, Iterator<T> {
	override fun iterator(): Iterator<T> = this

	override fun hasNext() = head != null

	override fun next(): T {
		val h = head ?: throw LaunchpadException("end of iterator")
		val v = h.next
		val value = h.value
		head = v
		return value
	}
}