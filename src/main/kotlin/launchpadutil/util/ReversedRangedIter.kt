package launchpadutil.util

class ReversedRangedIter<T>(
	private val array: Array<T>,
	private val start: Int = 0,
	private val len: Int = array.size - start
) :
	ReversibleIterator<T>,
	Cloneable {
	private var cursor = start + len

	override fun iterator() = this

	override fun hasNext() = cursor > start

	override fun next(): T = array[--cursor]

	override fun reverse() = RangedIter(array, start, len)

	override fun clone(): ReversedRangedIter<T> {
		return ReversedRangedIter(array, start, len)
	}

}