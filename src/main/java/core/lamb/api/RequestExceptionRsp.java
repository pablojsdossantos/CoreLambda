package core.lamb.api;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.Map;

/**
 *
 * @author Pablo JS dos Santos
 */
@RegisterForReflection
public class RequestExceptionRsp {
    private String exceptionCode;
    private String exceptionMessage;
    private Map<String, String> issues;

    public String getExceptionCode() {
        return exceptionCode;
    }

    public void setExceptionCode(String exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public Map<String, String> getIssues() {
        return issues;
    }

    public void setIssues(Map<String, String> issues) {
        this.issues = issues;
    }
}
