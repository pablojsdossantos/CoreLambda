package core.lamb.exceptions;

import java.util.Map;

/**
 *
 * @author Pablo JS dos Santos
 */
public interface StandardException {
    public String getExceptionCode();

    public Map<String, String> getIssues();

    public ExceptionLogLevel getLogLevel();
}
