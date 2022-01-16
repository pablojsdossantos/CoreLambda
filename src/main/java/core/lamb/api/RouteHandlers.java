package core.lamb.api;

import core.lamb.exceptions.ExceptionLogger;
import io.quarkus.arc.Arc;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author Pablo JS dos Santos
 */
@ApplicationScoped
public class RouteHandlers {
    private ExceptionLogger exceptionLogger;

    public RouteHandlers(ExceptionLogger exceptionLogger) {
        this.exceptionLogger = exceptionLogger;
    }

    public RouteActionBinder on(Router router, HttpMethod method, String path) {
        return new RouteActionBinder(Arc.container(), this.exceptionLogger, router, method, path);
    }
}
