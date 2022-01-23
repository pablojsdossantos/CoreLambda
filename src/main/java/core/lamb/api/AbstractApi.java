package core.lamb.api;

import core.lamb.exceptions.ExceptionLogger;
import core.lamb.parsers.JsonParser;
import io.vertx.ext.web.RoutingContext;
import javax.inject.Inject;
import javax.validation.Validator;
import org.eclipse.microprofile.context.ManagedExecutor;

/**
 *
 * @author Pablo JS dos Santos 
 */
public abstract class AbstractApi {
    @Inject
    ManagedExecutor executor;

    @Inject
    JsonParser jsonParser;

    @Inject
    Validator validator;

    @Inject
    ExceptionLogger logger;

    @Inject
    AbstractApiConfig configs;

    protected ApiRequestHandler handlerFor(RoutingContext context) {
        return new ApiRequestHandler(
            this.executor,
            this.jsonParser,
            this.configs.getMaxRequestSize(),
            this.validator,
            this.configs.getInputEncoding(),
            this.configs.getOutputEncoding(),
            this.logger,
            context,
            this.configs.getSessionIdFieldName()
        );
    }
}
