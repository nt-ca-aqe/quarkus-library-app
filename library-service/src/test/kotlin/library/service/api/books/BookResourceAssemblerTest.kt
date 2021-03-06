package library.service.api.books

import BookResourceAssembler
import io.mockk.every
import io.mockk.mockk
import library.service.business.books.domain.BookRecord
import library.service.business.books.domain.types.BookId
import library.service.business.books.domain.types.Borrower
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import utils.Books
import utils.classification.UnitTest
import java.net.URI
import java.time.OffsetDateTime
import javax.ws.rs.core.SecurityContext
import javax.ws.rs.core.UriInfo

@UnitTest
internal class BookResourceAssemblerTest {

    val cut = BookResourceAssembler()
    val id = BookId.generate()
    val book = Books.THE_MARTIAN
    val bookRecord = BookRecord(id, book)

    @Test
    fun `book with 'available' state is assembled correctly`() {

        val uriInfo = mockk<UriInfo>()
        val securityContext = mockk<SecurityContext>()
        val uri = URI("localhost")
        every { uriInfo.baseUri } returns uri
        every { securityContext.isUserInRole(any()) } returns true



        val resource = cut.toResource(uriInfo, bookRecord, securityContext)

        if (resource != null) {
            assertThat(resource.isbn).isEqualTo(book.isbn.toString())
            assertThat(resource.title).isEqualTo(book.title.toString())
            assertThat(resource.authors).isEqualTo(book.authors.map { it.toString() })
            assertThat(resource.borrowed).isNull()
        }

    }

    @Test
    fun `book with 'borrowed' state is assembled correctly`() {
        val borrowedBy = Borrower("Someone")
        val borrowedOn = OffsetDateTime.now()
        val borrowedBookRecord = bookRecord.borrow(borrowedBy, borrowedOn)
        val uriInfo = mockk<UriInfo>()
        val securityContext = mockk<SecurityContext>()
        val uri = URI("localhost")
        every { uriInfo.baseUri } returns uri
        every { securityContext.isUserInRole(any()) } returns true


        val resource = cut.toResource(uriInfo, borrowedBookRecord, securityContext)

        if (resource != null) {
            assertThat(resource.isbn).isEqualTo(book.isbn.toString())
            assertThat(resource.title).isEqualTo(book.title.toString())
            assertThat(resource.authors).isEqualTo(book.authors.map { it.toString() })
            assertThat(resource.borrowed).isNotNull()
            assertThat(resource.borrowed!!.by).isEqualTo("Someone")
            assertThat(resource.borrowed!!.on).isEqualTo(borrowedOn.toString())
        }
    }

}