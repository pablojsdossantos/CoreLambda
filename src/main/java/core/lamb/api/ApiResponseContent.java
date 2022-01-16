package core.lamb.api;

import java.util.Map;

/**
 *
 * @author Pablo JS dos Santos
 */
public class ApiResponseContent<T> {
    private String code;
    private String message;
    private String exceptionCode;
    private Map<String, String> issues;
    private T result;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getExceptionCode() {
        return exceptionCode;
    }

    public void setExceptionCode(String exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    public Map<String, String> getIssues() {
        return issues;
    }

    public void setIssues(Map<String, String> issues) {
        this.issues = issues;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ApiResponseContent{" + "code=" + code + ", message=" + message + ", exceptionCode=" + exceptionCode + ", issues=" + issues + ", result=" + result + '}';
    }
}
