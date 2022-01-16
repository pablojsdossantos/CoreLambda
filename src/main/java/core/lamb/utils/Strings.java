package core.lamb.utils;

/**
 *
 * @author Pablo JS dos Santos
 */
public class Strings {

    public static boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }

    public static boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }
}
