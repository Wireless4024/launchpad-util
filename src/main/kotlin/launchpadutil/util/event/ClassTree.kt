package launchpadutil.util.event

import org.jetbrains.annotations.NotNull

class ClassTree<T>(val clazz: Class<T>, root: ClassTree<*>? = null) {
	var root = root; private set
	private val tree = HashMap<Class<in T>, ClassTree<in T>>()
	private val values = mutableListOf<T>()

	fun <E : T> add(@NotNull e: E): ClassTree<in T> { // Charsequence
		val c = e!!::class.java
		if (c == clazz) {
			values.add(e)
			return this
		}
		val sup: Class<in T> = c.superclass as? Class<in T> ?: throw ClassCastException() // String

		return if (sup != clazz) {
			val sub = ClassTree(sup)
			val ret = sub.add(e)
			tree[sup] = sub
			ret
		} else {
			var v = tree[sup]
			if (v == null) {
				v = ClassTree(sup, this)
				tree[sup] = v
			}
			v.values.add(e)
			v
		}
	}

	fun getAll(clazz: Class<*>) {
		root?.getAll(clazz.superclass)
	}
}

fun main() {
	val root = ClassTree(CharSequence::class.java, ClassTree(Object::class.java))
}

private class Value<out T> {

}