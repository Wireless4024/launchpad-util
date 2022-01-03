package launchpadutil.config

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import launchpadutil.util.ImmutableArray
import launchpadutil.util.SingleIterator
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.StringWriter

class YamlFile(val file: File) : AutoCloseable {
	constructor(file: String) : this(File(file))

	private val yml: Yaml
	private lateinit var table: MutableMap<String, Any>

	init {
		when (file.extension) {
			"yml", "yaml", "config" -> {
				// pass
			}
			else -> throw UnsupportedOperationException("not a yaml")
		}
		yml = Yaml()
		runBlocking { load() }
	}

	operator fun get(key: String): Any? {
		var cur = 0
		val len = key.length
		var map: MutableMap<*, *>? = table
		while (cur < len && map != null) {
			val off = cur
			while (cur < len && key[cur++] != '.')
				continue
			val k = key.substring(off, if (cur < len) cur - 1 else cur)

			if (cur == len) return map[k]

			map = map[k] as? MutableMap<*, *>
		}
		return null
	}

	inline operator fun <reified T> invoke(key: String): T? {
		return when (T::class.java) {
			String::class.java -> getString(key) as? T
			Int::class.java -> getInt(key) as? T
			Long::class.java -> getLong(key) as? T
			Boolean::class.java -> get(key) as? T
			List::class.java -> getList<Any>(key) as? T
			else -> return get(key) as? T
		}
	}


	fun getBoolean(key: String) = get(key) == true
	fun getByte(key: String) = getInt(key).toByte()
	fun getShort(key: String) = getInt(key).toShort()
	fun getInt(key: String) = get(key) as Int
	fun getLong(key: String) = get(key).let { if (it is Int) it.toLong() else it as Long }
	fun getFloat(key: String) = getDouble(key).toFloat()
	fun getDouble(key: String) = get(key) as Double
	fun getString(key: String) = get(key).let { if (it is String?) it ?: "" else it.toString() }
	fun <T> getArray(key: String): ImmutableArray<T>? {
		return ImmutableArray(getList(key) ?: return null)
	}

	fun <T> getList(key: String, default: MutableList<T>? = null) = get(key).let {
		if (it == null) return@let default
		@Suppress("UNCHECKED_CAST") val list = when (it) {
			is MutableList<*> -> it
			is Array<*> -> it.toMutableList()
			else -> mutableListOf(it)
		} as MutableList<T>
		if (it !is MutableList<*>) set(key, list)
		list
	}

	fun getSection(key: String): HashMap<String, *>? {
		@Suppress("UNCHECKED_CAST")
		return get(key) as? HashMap<String, *>
	}

	operator fun set(key: String, value: Any?) {
		var cur = 0
		val len = key.length
		var prev = table
		var map: MutableMap<String, Any>? = table
		var k = ""// it's fine
		while (cur < len) {
			val off = cur
			while (cur < len && key[cur++] != '.')
				continue

			if (map == null) {
				map = mutableMapOf()
				prev[k] = map
			}

			k = key.substring(off, if (cur < len) cur - 1 else cur)

			if (cur == len) {
				if (value == null)
					map.remove(k)
				else
					map[k] = when (value) {
						is Byte -> value.toInt()
						is Short -> value.toInt()
						is Float -> value.toDouble()
						else -> value
					}
			}

			prev = map
			@Suppress("UNCHECKED_CAST") // this is fine
			map = map[k] as? MutableMap<String, Any>
		}
	}

	override fun toString() = StringWriter().use { yml.dumpAll(SingleIterator(table), it);it.toString() }

	suspend fun save() {
		withContext(Dispatchers.IO) {
			file.outputStream().writer(Charsets.UTF_8).buffered(file.length().coerceIn(1, 4096).toInt()).use {
				yml.dumpAll(SingleIterator(table), it)
			}
		}
	}

	suspend fun load() {
		withContext(Dispatchers.IO) {
			if (!file.exists())
				file.createNewFile()

			table = file.inputStream().reader(Charsets.UTF_8).buffered(file.length().coerceIn(1, 4096).toInt()).use {
				val obj: MutableMap<String, Any>? = yml.load(it)
				obj ?: mutableMapOf()
			}
		}
	}

	suspend fun reload() {
		save()
		load()
	}

	override fun close() {
		runBlocking {
			save()
		}
	}
}
