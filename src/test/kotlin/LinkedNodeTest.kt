import launchpadutil.util.LinkedNode
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class LinkedNodeTest {
    @Test
    fun testCreate() {
        val list = listOf("A", "B", "C", "D")
        val clone = LinkedNode.create(list.iterator()).asIterator().asSequence().toList()
        Assertions.assertEquals(list, clone)
    }

    @Test
    fun testEach() {
        val list = listOf("A", "B", "C", "D")
        val clone = LinkedNode.create(list.iterator())

        clone.forEach {

        }
    }
}