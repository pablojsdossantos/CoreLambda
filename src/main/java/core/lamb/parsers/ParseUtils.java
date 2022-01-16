package core.lamb.parsers;

import core.lamb.exceptions.UnparseableContentTypeException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

/**
 *
 * @author Pablo JS dos Santos
 */
public class ParseUtils {
    public static Duration parseDuration(String duration) {
        try {
            return Duration.parse(duration);
        } catch (Exception e) {
            throw new UnparseableContentTypeException("CLPPUPD21", e);
        }
    }

    public static String parseDuration(Duration duration) {
        return Optional.ofNullable(duration)
            .map(Duration::toString)
            .orElse(null);
    }

    public static Instant parseInstant(String instant) {
        try {
            return Instant.parse(instant);
        } catch (Exception e) {
            throw new UnparseableContentTypeException("CLPPUPI35", e);
        }
    }

    public static String parseInstant(Instant instant) {
        return Optional.ofNullable(instant)
            .map(Instant::toString)
            .orElse(null);
    }

    public static LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            throw new UnparseableContentTypeException("CLPPUPD50", e);
        }
    }

    public static String parseDate(LocalDate date) {
        return Optional.ofNullable(date)
            .map(DateTimeFormatter.ISO_LOCAL_DATE::format)
            .orElse(null);
    }

    public static <T> T parseByType(String value, Class<T> type) {
        if (value == null) {
            return null;
        }

        if (type.equals(Integer.class) || type.equals(int.class)) {
            return (T) (Integer) Integer.parseInt(value);
        }

        if (type.equals(Double.class) || type.equals(double.class)) {
            return (T) (Double) Double.parseDouble(value);
        }

        if (type.equals(Long.class) || type.equals(long.class)) {
            return (T) (Long) Long.parseLong(value);
        }

        if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return (T) (Boolean) Boolean.parseBoolean(value);
        }

        if (type.equals(Short.class) || type.equals(short.class)) {
            return (T) (Short) Short.parseShort(value);
        }

        if (type.equals(Character.class) || type.equals(char.class)) {
            if (value.length() != 1) {
                throw new UnparseableContentTypeException("CLPPUPBT85", null);
            }

            return (T) (Character) value.charAt(0);
        }

        if (type.isEnum()) {
            Class t = type;
            return (T) Enum.valueOf(t, value);
        }

        if (type.equals(String.class)) {
            return (T) value;
        }

        if (type.equals(Instant.class)) {
            return (T) parseInstant(value);
        }

        if (type.equals(LocalDate.class)) {
            return (T) parseDate(value);
        }

        if (type.equals(Duration.class)) {
            return (T) parseDuration(value);
        }

        throw new UnparseableContentTypeException("CLPPUPBT112", null);
    }

    public static UUID parseUUID(String uuid) {
        try {
            return UUID.fromString(uuid);
        } catch (Exception e) {
            throw new UnparseableContentTypeException("CLPPUPU119", e);
        }
    }
}
