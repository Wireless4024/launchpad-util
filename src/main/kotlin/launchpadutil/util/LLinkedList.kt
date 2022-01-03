package launchpadutil.util

import java.util.function.Consumer

class LLinkedList<T>(vararg data: T) : List<T>, Iterable<T> {
	private var head: LinkedNode<T>? = null
	private lateinit var tail: LinkedNode<T>

	override var size = data.size
		private set

	init {
		if (data.isNotEmpty()) {
			val h = LinkedNode(data[0])
			head = h
			var latest = h
			for (i in 1..data.lastIndex) {
				val l = LinkedNode(data[i])
				latest.next = l
				latest = l
			}
			tail = latest
		}
	}

	fun add(value: T) {
		val node = LinkedNode(value)
		if (head == null) {
			head = node
			tail = node
			size = 1
			return
		}
		tail.next = node
		tail = node
		++size
	}

	fun replaceLast(value: T) {
		tail.value = value
	}

	fun distinct(): LLinkedList<T> {
		val new = LLinkedList<T>()
		for (i in this)
			if (!new.contains(i))
				new.add(i)
		return new
	}

	override fun contains(element: T): Boolean {
		return when (size) {
			0 -> false
			1 -> head?.value == element
			else -> indexOf(element) != -1
		}
	}

	override fun containsAll(elements: Collection<T>): Boolean {
		for (i in elements)
			if (!contains(i)) return false
		return true
	}

	override fun get(index: Int): T {
		TODO("Not yet implemented")
	}

	fun first(): T {
		return head!!.value
	}

	override fun indexOf(element: T): Int {
		for ((i, v) in this.withIndex())
			if (element == v) return i
		return -1
	}

	override fun forEach(action: Consumer<in T>) {
		LinkedNode.each(head, action::accept)
	}

	override fun isEmpty() = size == 0

	override fun iterator(): Iterator<T> = Iter(head)

	override fun lastIndexOf(element: T): Int {
		TODO("Not yet implemented")
	}

	override fun listIterator(): ListIterator<T> {
		TODO("Not yet implemented")
	}

	override fun listIterator(index: Int): ListIterator<T> {
		TODO("Not yet implemented")
	}

	override fun subList(fromIndex: Int, toIndex: Int): List<T> {
		TODO("Not yet implemented")
	}

	override fun toString(): String {
		return iterator().asSequence().joinToString(", ", "[", "]")
	}
}