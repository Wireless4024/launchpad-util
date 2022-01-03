package launchpadutil.util

internal open class LinkedNode<T>(value: T, var next: LinkedNode<T>? = null) {
	var value = value
		internal set

	override fun toString() = "Node($value)"

	open fun forEach(action: (T) -> Unit) {
		each(this, action)
	}

	open fun asIterator(): Iterator<T> {
		return object : Iterator<T> {
			var cur: LinkedNode<T>? = this@LinkedNode
			override fun hasNext() = cur != null

			override fun next(): T {
				val c = cur!!
				cur = c.next
				return c.value
			}
		}
	}

	@Suppress("MemberVisibilityCanBePrivate")
	companion object {
		val EMPTY: LinkedNode<*> = object : LinkedNode<Unit>(Unit) {
			override fun forEach(action: (Unit) -> Unit) {}
			override fun asIterator(): Iterator<Unit> {
				return object : Iterator<Unit> {
					override fun hasNext() = false
					override fun next() = Unit
				}
			}
		}

		inline fun <T> each(head: LinkedNode<T>?, action: (T) -> Unit) {
			var h = head
			while (h != null) {
				action(h.value)
				h = h.next
			}
		}

		inline fun <T> eachU(head: LinkedNode<T>?, action: (T) -> Unit) {
			var h = head
			var p = h
			while (p != null) {
				p.next = null
				if (h == null) break
				action(h.value)
				p = h
				h = h.next
			}
		}

		fun <T> unlink(head: LinkedNode<T>?, value: T) {
			var h = head
			var p = h
			while (h != null) {
				if (h.value == value) {
					p?.next = h.next
					return
				}
				p = h
				h = h.next
			}
		}

		fun <T> create(source: Iterator<T>): LinkedNode<T> {
			@Suppress("UNCHECKED_CAST")
			if (!source.hasNext()) return EMPTY as LinkedNode<T>

			val head = LinkedNode(source.next())
			var current = head
			while (source.hasNext()) {
				val next = LinkedNode(source.next())
				current.next = next
				current = next
			}
			return head
		}
	}
}