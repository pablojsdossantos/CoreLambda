package core.lamb.exceptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.CompletionException;

/**
 *
 * @author Pablo JS dos Santos
 */
public class ExceptionUtils {
    public static boolean isCausedBy(Throwable throwable, Class cause) {
        return lookup(throwable, cause) != null;
    }

    public static <T> T lookup(Throwable throwable, Class<T> clazz) {
        if (throwable == null || clazz.isInstance(throwable)) {
            return (T) throwable;
        }

        return lookup(throwable.getCause(), clazz);
    }

    public static void throwAgain(Throwable throwable) {
        throwable = skipCompletionException(throwable);
        RuntimeException runtimeException = lookup(throwable, RuntimeException.class);
        throw runtimeException;
    }

    public static Throwable skipCompletionException(Throwable throwable) {
        if (throwable instanceof CompletionException) {
            CompletionException completionException = (CompletionException) throwable;
            return completionException.getCause();
        }

        return throwable;
    }

    public static String getStackTrace(Throwable throwable) {
        try ( ByteArrayOutputStream output = new ByteArrayOutputStream();  PrintStream stream = new PrintStream(output)) {
            throwable.printStackTrace(stream);
            return stream.toString();
        } catch (IOException ex) {
            return "Failed to get stack trace";
        }
    }
}
