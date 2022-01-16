package core.lamb.api;

import java.util.Optional;

/**
 *
 * @author Pablo JS dos Santos
 */
public class ApiRequest {
    private Optional<ApiSession> session;
    private Object body;
    private Object queryParameters;
    private Object pathParameters;

    public Optional<ApiSession> getSession() {
        return session;
    }

    public void setSession(Optional<ApiSession> session) {
        this.session = session;
    }

    public <T> T getBody() {
        return (T) body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public void setQueryParameters(Object parameters) {
        this.queryParameters = parameters;
    }

    public <T> T getQueryParameters() {
        return (T) queryParameters;
    }

    public <T> T getPathParameters() {
        return (T) pathParameters;
    }

    public void setPathParameters(Object pathParameters) {
        this.pathParameters = pathParameters;
    }
}
