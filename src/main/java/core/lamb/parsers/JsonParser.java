package core.lamb.parsers;

/**
 *
 * @author Pablo JS dos Santos
 */
public interface JsonParser {
    public <T> T fromJson(String json, Class<T> rawType);

    public <T> T fromJson(String json, Class<T> rawType, Class<?>... parameterType);

    public String toJson(Object object);
}
