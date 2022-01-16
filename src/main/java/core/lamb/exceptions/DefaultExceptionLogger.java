package core.lamb.exceptions;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pablo JS dos Santos
 */
public class DefaultExceptionLogger implements ExceptionLogger {
    private Logger defaultLogger;

    public DefaultExceptionLogger() {
        this.defaultLogger = Logger.getLogger("default.exception.logger");
    }

    @Override
    public void handle(Throwable throwable) {
        if (throwable == null) {
            this.defaultLogger.log(Level.SEVERE, "Unexpectedly NULL excetpion caught");
        } else {
            Level level = Level.SEVERE;
            String message = throwable.getLocalizedMessage();

            if (throwable instanceof StandardException) {
                StandardException standardException = (StandardException) throwable;
                level = this.translate(standardException.getLogLevel());

                message = String.format("Exception Code: %s \nMessage: %s \nIssues: %s",
                    standardException.getExceptionCode(), throwable.getLocalizedMessage(), standardException.getIssues());
            }

            if (level != null) {
                this.defaultLogger.log(level, message, throwable);
            }
        }
    }

    private Level translate(ExceptionLogLevel level) {
        switch (level) {
            case INFO:
                return Level.INFO;

            case WARN:
                return Level.WARNING;

            case ERROR:
                return Level.SEVERE;

            default:
                return null;
        }
    }
}
