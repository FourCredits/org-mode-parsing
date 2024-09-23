import org.example.*
import kotlin.test.*

class OrgTest {
    @Test
    fun `empty document`() = orgDocument.successfullyParses(
        "", OrgDocument(emptyList())
    )

    @Test
    fun `document consisting of blank lines, ending in newlines`() = orgDocument.successfullyParses(
        " \n\n\t\n", OrgDocument(listOf(" ", "", "\t"))
    )

    @Test
    fun `document consisting of blank lines with no final new line`() =
        orgDocument.successfullyParses(
            " \n\n\t", OrgDocument(listOf(" ", "", "\t"))
        )
}