@file:Suppress("SpellCheckingInspection")

package launchpadutil.util

class RangedIter<T>(private val array: Array<T>, private val start: Int = 0, len: Int = array.size - start) :
	ReversibleIterator<T>,
	Cloneable {
	var cursor = start
		private set

	private val end = start + len

	override fun iterator() = this

	override fun hasNext() = cursor < end

	override fun next(): T = array[cursor++]

	override fun reverse() = ReversedRangedIter(array, start, end - start)

	override fun clone(): RangedIter<T> {
		return RangedIter(array, start, end - start)
	}
}