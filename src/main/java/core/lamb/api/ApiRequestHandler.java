package core.lamb.api;

import core.lamb.exceptions.ExceptionLogger;
import core.lamb.exceptions.RequestException;
import core.lamb.exceptions.UnparseableContentTypeException;
import core.lamb.parsers.JsonParser;
import core.lamb.parsers.ParseUtils;
import core.lamb.utils.Strings;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.context.ManagedExecutor;

/**
 *
 * @author Pablo JS dos Santos
 */
public class ApiRequestHandler {
    private final static String REQUEST_SIZE_EXCEPTION_CODE = "CLAARHRBAS323";

    private ManagedExecutor executor;

    private Function<String, CompletionStage<Optional<ApiSession>>> sessionLoader;
    private boolean requireAuthenticatedSession;
    private List<String> requestAccessLevel;
    private AccessLevelVerificationMethod accessLevelVerificationMethod;

    private long maxRequestSize;
    private ApiMessageType requestBodyType;
    private Class requestBodyClass;
    private Class queryParametersClass;
    private Class pathParametersClass;

    private JsonParser jsonParser;
    private Validator validator;
    private Charset inputEncoding;
    private Charset outputEncoding;

    private ExceptionLogger exceptionLogger;
    private RoutingContext context;
    private String sessionIdFieldName;

    public ApiRequestHandler(
        ManagedExecutor executor,
        JsonParser jsonParser,
        long maxRequestSize,
        Validator validator,
        Charset inputEncoding,
        Charset outputEncoding,
        ExceptionLogger logger,
        RoutingContext context,
        String sessionIdFieldName) {
        this.executor = executor;
        this.jsonParser = jsonParser;
        this.maxRequestSize = maxRequestSize;
        this.validator = validator;
        this.inputEncoding = inputEncoding;
        this.outputEncoding = outputEncoding;
        this.exceptionLogger = logger;
        this.context = context;
        this.sessionIdFieldName = sessionIdFieldName;
    }

    public ApiRequestHandler withExceptionLogger(ExceptionLogger logger) {
        this.exceptionLogger = logger;
        return this;
    }

    public ApiRequestHandler withInputEncoding(Charset charset) {
        this.inputEncoding = charset;
        return this;
    }

    public ApiRequestHandler withOutputEncoding(Charset charset) {
        this.outputEncoding = charset;
        return this;
    }

    public ApiRequestHandler withJsonParser(JsonParser jsonParser) {
        this.jsonParser = jsonParser;
        return this;
    }

    public ApiRequestHandler withValidator(Validator validator) {
        this.validator = validator;
        return this;
    }

    public ApiRequestHandler authenticated() {
        this.requireAuthenticatedSession = true;
        return this;
    }

    public ApiRequestHandler withAccessLevel(String accessLevel) {
        return this.withAllRequiredLevels(accessLevel);
    }

    public ApiRequestHandler withAnyRequiredLevel(String... accessLevel) {
        this.requestAccessLevel = Arrays.asList(accessLevel);
        this.accessLevelVerificationMethod = AccessLevelVerificationMethod.ANY;
        return this;
    }

    public ApiRequestHandler withAllRequiredLevels(String... accessLevel) {
        this.requestAccessLevel = Arrays.asList(accessLevel);
        this.accessLevelVerificationMethod = AccessLevelVerificationMethod.ALL;
        return this;
    }

    public ApiRequestHandler expectingJsonBody(Class requestBodyClass) {
        this.requestBodyClass = requestBodyClass;
        this.requestBodyType = ApiMessageType.JSON;
        return this;
    }

    public ApiRequestHandler expectingQueryParameters(Class clazz) {
        this.queryParametersClass = clazz;
        return this;
    }

    public ApiRequestHandler expectingPathParameters(Class clazz) {
        this.pathParametersClass = clazz;
        return this;
    }

    public ApiRequestHandler withLimitedSize(long maxRequestSize) {
        this.maxRequestSize = maxRequestSize;
        return this;
    }

    public void invoke(Function<ApiRequest, CompletionStage<ApiResponse>> function) {
        ApiRequest request = new ApiRequest();

        CompletableFuture.supplyAsync(() -> request, this.executor)
            .thenCompose(this::loadSession)
            .thenApply(this::verifyAuthentication)
            .thenApply(this::verifyAccessLevel)
            .thenCompose(this::parseRequestBody)
            .thenApply(this::parsePathParameters)
            .thenApply(this::parseQueryParameters)
            .thenCompose(function::apply)
            .exceptionally(this::handleException)
            .thenAccept(this::sendResult);
    }

    private ApiResponse handleException(Throwable throwable) {
        if (throwable instanceof CompletionException) {
            CompletionException completionException = (CompletionException) throwable;
            throwable = completionException.getCause();
        }

        this.exceptionLogger.handle(throwable);

        ApiResponse response;

        if (throwable instanceof RequestException) {
            RequestException exception = (RequestException) throwable;
            response = ApiResponse.fail(exception);
        } else {
            response = ApiResponse.fail();
        }

        return response;
    }

    private void sendResult(ApiResponse apiResponse) {
        HttpServerResponse serverResponse = this.context.response();

        try {
            serverResponse.setStatusCode(apiResponse.getHttpStatusCode());

            this.setResponseHeaders(serverResponse, apiResponse);
            this.setResponseCookies(serverResponse, apiResponse);
            this.writeResponseBody(serverResponse, apiResponse);

            serverResponse.end();
        } catch (Exception e) {
            this.exceptionLogger.handle(e);
            serverResponse.setStatusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
            serverResponse.end();
        } finally {
            if (this.shouldForceCloseConnection(apiResponse)) {
                this.context.request().connection().close();
            }
        }
    }

    private boolean shouldForceCloseConnection(ApiResponse apiResponse) {
        Object content = apiResponse.getContent();

        if (content instanceof RequestExceptionRsp) {
            RequestExceptionRsp exceptionResult = (RequestExceptionRsp) content;
            return REQUEST_SIZE_EXCEPTION_CODE.equals(exceptionResult.getExceptionCode());
        }

        return false;
    }

    private void writeResponseBody(HttpServerResponse serverResponse, ApiResponse apiResponse) {
        if (apiResponse.getMessageType() == ApiMessageType.JSON) {
            this.writeResponseAsJson(serverResponse, apiResponse);
        }
    }

    private void writeResponseAsJson(HttpServerResponse serverResponse, ApiResponse apiResponse) {
        Object result = apiResponse.getContent();

        if (result != null) {
            String json = this.jsonParser.toJson(result);
            Buffer buffer = Buffer.buffer(json.getBytes(this.outputEncoding));

            serverResponse.putHeader("Content-Length", Integer.toString(buffer.length()));
            serverResponse.putHeader("Content-Type", String.format("%s; charset=%s", MediaType.APPLICATION_JSON, this.outputEncoding.name()));
            serverResponse.write(buffer);
        }
    }

    private void setResponseHeaders(HttpServerResponse serverResponse, ApiResponse apiResponse) {
        Map<String, String> headers = apiResponse.getHeaders();
        if (headers != null) {
            headers.entrySet()
                .forEach(entry -> serverResponse.putHeader(entry.getKey(), entry.getValue()));
        }
    }

    private void setResponseCookies(HttpServerResponse serverResponse, ApiResponse apiResponse) {
        Map<String, String> cookies = apiResponse.getCookies();
        if (cookies != null) {
            cookies.entrySet()
                .stream()
                .map(entry -> Cookie.cookie(entry.getKey(), entry.getValue()))
                .forEach(serverResponse::addCookie);
        }
    }

    private CompletionStage<ApiRequest> loadSession(ApiRequest request) {
        if (this.sessionLoader != null) {
            String sessionId = this.resolveSessionId();

            if (sessionId != null) {
                return this.sessionLoader.apply(sessionId)
                    .thenApply(apiSession -> {
                        request.setSession(apiSession);
                        return request;
                    });
            }
        }

        request.setSession(Optional.empty());
        return CompletableFuture.completedFuture(request);
    }

    private String resolveSessionId() {
        if (Strings.isNullOrBlank(this.sessionIdFieldName)) {
            return null;
        }

        String header = this.context.request().getHeader(this.sessionIdFieldName);
        if (Strings.isNotBlank(header)) {
            return header;
        }

        Cookie cookie = this.context.request().getCookie(this.sessionIdFieldName);
        return cookie == null ? null : cookie.getValue();
    }

    private ApiRequest verifyAuthentication(ApiRequest request) {
        if (this.requireAuthenticatedSession) {
            boolean hasAuthenticatedSession = request.getSession()
                .filter(ApiSession::isAuthenticated)
                .isPresent();

            if (!hasAuthenticatedSession) {
                throw new RequestException(Response.Status.UNAUTHORIZED, "CLAARHA218", null, "Access Denied");
            }
        }

        return request;
    }

    private ApiRequest verifyAccessLevel(ApiRequest request) {
        boolean accessGranted = true;

        List<String> userPermissions = request.getSession()
            .map(ApiSession::getAccessLevel)
            .orElse(Collections.EMPTY_LIST);

        if (this.accessLevelVerificationMethod == AccessLevelVerificationMethod.ANY) {
            accessGranted = this.requestAccessLevel.stream()
                .anyMatch(userPermissions::contains);
        } else if (this.accessLevelVerificationMethod == AccessLevelVerificationMethod.ALL) {
            accessGranted = this.requestAccessLevel.stream()
                .allMatch(userPermissions::contains);
        }

        if (accessGranted) {
            return request;
        }

        throw new RequestException(Response.Status.UNAUTHORIZED, "CLAARHVAL248", null, "Access Denied");
    }

    private CompletionStage<ApiRequest> parseRequestBody(ApiRequest request) {
        if (this.requestBodyType == null) {
            return CompletableFuture.completedFuture(request);
        }

        switch (this.requestBodyType) {
            case JSON:
                return this.readBodyAsJson(request);

            default:
                throw new RequestException("CLAARHPRB261", "Unsupported content type");
        }
    }

    private CompletionStage<ApiRequest> readBodyAsJson(ApiRequest request) {
        return this.readBodyAsString()
            .thenApply(jsonAsString -> this.jsonParser.fromJson(jsonAsString, this.requestBodyClass))
            .thenApply(this::validate)
            .thenApply(validBody -> {
                request.setBody(validBody);
                return request;
            });
    }

    private Object validate(Object object) {
        if (this.validator != null && object != null) {
            Set<ConstraintViolation<Object>> violations = this.validator.validate(object);

            if (violations != null && !violations.isEmpty()) {
                HashMap<String, String> issues = new HashMap<>();

                for (ConstraintViolation<Object> violation : violations) {
                    issues.put(violation.getPropertyPath().toString(), violation.getMessage());
                }

                throw new RequestException(Response.Status.BAD_REQUEST, "CLAARHV296", issues, "Validation failed");
            }
        }

        return object;
    }

    private CompletionStage<String> readBodyAsString() {
        CompletableFuture<String> future = new CompletableFuture<>();
        Buffer requestBuffer = Buffer.buffer();

        HttpServerRequest request = this.context.request();
        request.endHandler((event) -> future.complete(requestBuffer.toString(this.inputEncoding)));
        request.exceptionHandler(future::completeExceptionally);

        request.handler(buffer -> {
            if (requestBuffer.length() + buffer.length() > this.maxRequestSize) {
                request.pause();
                future.completeExceptionally(new RequestException(REQUEST_SIZE_EXCEPTION_CODE, "Request size limit exceeded"));
            } else {
                requestBuffer.appendBuffer(buffer);
            }
        });

        request.fetch(this.maxRequestSize);

        return future;
    }

    private ApiRequest parsePathParameters(ApiRequest request) {
        if (this.pathParametersClass == null) {
            return request;
        }

        Object instance = this.instantiateParametersForm(this.pathParametersClass);
        Map<String, String> parameters = this.context.pathParams();

        if (parameters != null && !parameters.isEmpty()) {
            this.populateInstance(instance, parameters);
        }

        this.validate(instance);
        request.setPathParameters(instance);
        return request;
    }

    private ApiRequest parseQueryParameters(ApiRequest request) {
        if (this.queryParametersClass == null) {
            return request;
        }

        Object instance = this.instantiateParametersForm(this.queryParametersClass);
        MultiMap parameters = this.context.queryParams(this.inputEncoding);

        if (parameters != null && !parameters.isEmpty()) {
            Map<String, String> map = new HashMap<>();
            parameters.forEach(entry -> map.put(entry.getKey(), entry.getValue()));

            this.populateInstance(instance, map);
        }

        this.validate(instance);
        request.setQueryParameters(instance);
        return request;
    }

    private Object instantiateParametersForm(Class clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException
            | SecurityException
            | InstantiationException
            | IllegalAccessException
            | IllegalArgumentException
            | InvocationTargetException ex) {
            throw new RequestException("CLAARHIQPF327", "Failed to instantiate class " + clazz, ex);
        }
    }

    private void populateInstance(Object instance, Map<String, String> parameters) {
        Field[] fields = instance.getClass().getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);

                String strValue = parameters.get(field.getName());
                if (Strings.isNotBlank(strValue)) {
                    Object objValue = ParseUtils.parseByType(strValue, field.getType());
                    field.set(instance, objValue);
                }
            } catch (IllegalAccessException | IllegalArgumentException e) {
                throw new UnparseableContentTypeException("CLAARHPI35", e);
            }
        }
    }
}
