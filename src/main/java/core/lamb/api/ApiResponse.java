package core.lamb.api;

import core.lamb.exceptions.RequestException;
import java.util.Map;
import javax.ws.rs.core.Response;

/**
 *
 * @author Pablo JS dos Santos
 */
public class ApiResponse {
    public static final String SUCCESS_CODE = "OK";
    public static final String SUCCESS_MESSAGE = "Success";
    public static final String FAIL_CODE = "FAIL";

    private static final String FAIL_MESSAGE = "Unexpected error";

    private Map<String, String> headers;
    private Map<String, String> cookies;
    private Object content;
    private int httpStatusCode;
    private ApiMessageType messageType;

    public ApiResponse() {
        this.content = new ApiResponseContent();
    }

    public ApiMessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(ApiMessageType messageType) {
        this.messageType = messageType;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public int getHttpStatusCode() {
        return this.httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public static ApiResponse success() {
        ApiResponse response = new ApiResponse();
        response.setHttpStatusCode(Response.Status.OK.getStatusCode());

        return response;
    }

    public static ApiResponse success(Object result) {
        ApiResponse response = success();
        response.setMessageType(ApiMessageType.JSON);
        response.setContent(result);

        return response;
    }

    public static ApiResponse fail(RequestException exception) {
        ApiResponse response = new ApiResponse();
        response.setHttpStatusCode(exception.getHttpStatusCode().getStatusCode());

        RequestExceptionRsp responseValue = new RequestExceptionRsp();
        responseValue.setExceptionCode(exception.getExceptionCode());
        responseValue.setExceptionMessage(exception.getMessage());
        responseValue.setIssues(exception.getIssues());

        response.setContent(responseValue);
        response.setMessageType(ApiMessageType.JSON);

        return response;
    }

    public static ApiResponse fail() {
        ApiResponse response = new ApiResponse();
        response.setHttpStatusCode(Response.Status.BAD_REQUEST.getStatusCode());

        RequestExceptionRsp responseValue = new RequestExceptionRsp();
        responseValue.setExceptionCode("???");
        responseValue.setExceptionMessage("Unexpected Excetpion");

        response.setContent(responseValue);
        response.setMessageType(ApiMessageType.JSON);

        return response;
    }
}
