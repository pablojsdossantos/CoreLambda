package core.lamb.api;

import java.util.List;

/**
 *
 * @author Pablo JS dos Santos
 */
public class ApiSession {
    private String id;
    private String userId;
    private List<String> accessLevel;
    private boolean authenticated;
    private String body;

    public ApiSession() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(List<String> accessLevel) {
        this.accessLevel = accessLevel;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
