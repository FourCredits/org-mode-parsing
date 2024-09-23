import org.example.*
import kotlin.test.*

fun <T> Parser<T>.successfullyParses(input: CharSequence, expected: T, message: String? = null) {
    val result = parse(input)
    assertIs<SuccessfulParse<T>>(result, "failed to parse")
    assertTrue(result.remaining.isEmpty(), "didn't parse the whole input")
    assertEquals(expected, result.value, message)
}