package launchpadutil.util

interface ReversibleIterator<T> : Iterble<T> {
	fun reverse(): ReversibleIterator<T>
}