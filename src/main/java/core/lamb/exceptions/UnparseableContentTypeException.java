package core.lamb.exceptions;

/**
 *
 * @author Pablo JS dos Santos
 */
public class UnparseableContentTypeException extends RequestException {
    public UnparseableContentTypeException(String exceptionCode, Throwable cause) {
        super(exceptionCode, "Unparseable content type", cause);
    }
}
