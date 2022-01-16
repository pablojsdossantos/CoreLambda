package core.lamb.exceptions;

import java.util.Map;

/**
 *
 * @author Pablo JS dos Santos
 */
public class UncheckedException extends RuntimeException implements StandardException {
    private String code;
    private Map<String, String> issues;
    private ExceptionLogLevel logLevel;

    public UncheckedException(String code, String message, Throwable cause) {
        this(ExceptionLogLevel.ERROR, code, message, null, cause);
    }

    public UncheckedException(String code, String message) {
        this(ExceptionLogLevel.ERROR, code, message, null, null);
    }

    public UncheckedException(String code, String message, Map<String, String> issues) {
        this(ExceptionLogLevel.ERROR, code, message, issues, null);
    }

    public UncheckedException(String code) {
        this(ExceptionLogLevel.ERROR, code, null, null, null);
    }

    public UncheckedException(ExceptionLogLevel logLevel, String code, String message, Map<String, String> issues, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.issues = issues;
        this.logLevel = logLevel;
    }

    @Override
    public String getExceptionCode() {
        return this.code;
    }

    @Override
    public Map<String, String> getIssues() {
        return this.issues;
    }

    @Override
    public ExceptionLogLevel getLogLevel() {
        return this.logLevel;
    }
}
