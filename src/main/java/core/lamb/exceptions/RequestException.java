package core.lamb.exceptions;

import java.util.Map;
import javax.ws.rs.core.Response;

/**
 *
 * @author Pablo JS dos Santos
 */
public class RequestException extends UncheckedException {
    private Response.Status httpStatusCode;

    public RequestException(String exceptionCode, String message) {
        this(ExceptionLogLevel.WARN, Response.Status.BAD_REQUEST, exceptionCode, null, message, null);
    }

    public RequestException(String exceptionCode, Map<String, String> issues) {
        this(ExceptionLogLevel.WARN, Response.Status.BAD_REQUEST, exceptionCode, issues, null, null);
    }

    public RequestException(String exceptionCode, Map<String, String> issues, String message) {
        this(ExceptionLogLevel.WARN, Response.Status.BAD_REQUEST, exceptionCode, issues, message, null);
    }

    public RequestException(Response.Status httpStatusCode, String exceptionCode, Map<String, String> issues) {
        this(ExceptionLogLevel.WARN, httpStatusCode, exceptionCode, issues, null, null);
    }

    public RequestException(Response.Status httpStatusCode, String exceptionCode, Map<String, String> issues, String message) {
        this(ExceptionLogLevel.WARN, httpStatusCode, exceptionCode, issues, message, null);
    }

    public RequestException(String exceptionCode, String message, Throwable cause) {
        this(ExceptionLogLevel.WARN, Response.Status.BAD_REQUEST, exceptionCode, null, message, cause);
    }

    public RequestException(ExceptionLogLevel logLevel, String code, String message, Throwable cause) {
        this(logLevel, Response.Status.BAD_REQUEST, code, null, message, cause);
    }

    public RequestException(ExceptionLogLevel logLevel, Response.Status httpStatusCode, String exceptionCode, Map<String, String> issues, String message, Throwable cause) {
        super(logLevel, exceptionCode, message, issues, cause);
        this.httpStatusCode = httpStatusCode;
    }

    public Response.Status getHttpStatusCode() {
        return httpStatusCode;
    }
}
