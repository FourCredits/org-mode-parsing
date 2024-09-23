import org.example.*
import org.junit.jupiter.api.Assertions.*
import kotlin.test.*

// a lot of parsing relies on functions so small, they're obviously correct. as such, i haven't written a complete list
// of tests here. these are just for problems i discover in the course of writing the org parser
class ParsingTest {
    @Test fun `zeroOrMore, with no elements, returns an empty sequence`() {
        assertEquals(SuccessfulParse(emptySequence<Char>(), ""), zeroOrMore(char('t')).parse(""))
    }

    @Test fun `zeroOrMore, with elements, returns a sequence of those elements`() {
        val result = zeroOrMore(char('t')).parse("tt")
        assertIs<SuccessfulParse<Sequence<Char>>>(result)
        assertContentEquals(sequenceOf('t', 't'), result.value)
        assertTrue(result.remaining.isEmpty())
    }
}