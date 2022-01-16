package core.lamb.api;

import core.lamb.exceptions.ExceptionLogger;
import io.quarkus.arc.ArcContainer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * @author Pablo JS dos Santos
 */
public class RouteActionBinder {
    private ArcContainer container;
    private ExceptionLogger exceptionLogger;
    private Router router;
    private HttpMethod method;
    private String path;

    public RouteActionBinder(ArcContainer container, ExceptionLogger exceptionLogger, Router router, HttpMethod method, String path) {
        this.container = container;
        this.exceptionLogger = exceptionLogger;
        this.router = router;
        this.method = method;
        this.path = path;
    }

    public <T> void bind(Class<T> clazz, Function<T, Consumer<RoutingContext>> handlerMethodSupplier) {
        this.router.route(this.method, this.path)
            .handler(routingContext -> {
                System.out.println("new routingContext = " + routingContext);
                routingContext.request().pause();

                this.container.requestContext().activate();
                routingContext.addEndHandler(asyncResult -> {
                    if (asyncResult.failed()) {
                        this.exceptionLogger.handle(asyncResult.cause());
                    }

                    this.container.requestContext().terminate();
                });

                T instance = this.container.instance(clazz).get();
                Consumer<RoutingContext> businessLogic = handlerMethodSupplier.apply(instance);
                businessLogic.accept(routingContext);
            });
    }
}
