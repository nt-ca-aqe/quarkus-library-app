
import library.service.api.ErrorDescription
import library.service.business.exceptions.MalformedValueException
import org.apache.http.HttpStatus
import java.time.Clock
import java.time.OffsetDateTime
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

@Provider
@ApplicationScoped
class MalformedValueExceptionHandler(
        private val clock: Clock) : ExceptionMapper<MalformedValueException> {

    override fun toResponse(p0: MalformedValueException?): Response? {

        println("Message + ${p0?.message}")

        val errorDescription = p0?.message?.let {
            ErrorDescription(
                    status = HttpStatus.SC_BAD_REQUEST,
                    error = "Bad Request",
                    timestamp = OffsetDateTime.now(clock).toString(),
                    correlationId = "",
                    message = it
            )
        }
        return Response.status(HttpStatus.SC_BAD_REQUEST).entity(errorDescription).build()
    }

}