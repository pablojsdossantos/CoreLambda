package core.lamb.exceptions;

import io.quarkus.arc.DefaultBean;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

/**
 *
 * @author Pablo JS dos Santos
 */
@ApplicationScoped
public class ExceptionLoggerProvider {
    private DefaultExceptionLogger logger;

    public ExceptionLoggerProvider() {
        this.logger = new DefaultExceptionLogger();
    }

    @Produces
    @DefaultBean
    public ExceptionLogger getLogger() {
        return this.logger;
    }
}
