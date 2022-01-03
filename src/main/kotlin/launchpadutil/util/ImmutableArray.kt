package launchpadutil.util

class ImmutableArray<T>(private val parent: List<T>) {
	operator fun get(index: Int) = parent[index]
	val size = parent.size
	fun contains(element: T) = parent.contains(element)
	fun indexOf(element: T) = parent.indexOf(element)
	fun isEmpty() = parent.isEmpty()
	fun iterator() = parent.iterator()
	fun lastIndexOf(element: T) = parent.lastIndexOf(element)
}