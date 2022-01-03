package launchpadutil.util

class SingleIterator<T>(val value: T) : Iterator<T> {
	private var available = true
	override fun hasNext() = available

	override fun next(): T {
		available = false
		return value
	}
}