package core.lamb.parsers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import core.lamb.exceptions.UnparseableContentTypeException;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.UUID;

/**
 *
 * @author Pablo JS dos Santos
 */
public class GsonJsonParser implements JsonParser {
    private Gson gson;

    public GsonJsonParser() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(UUID.class, new UUIDSerializer());
        builder.registerTypeAdapter(Instant.class, new InstantSerializer());

        this.gson = builder.create();
    }

    @Override
    public <T> T fromJson(String json, Class<T> rawType) {
        try {
            return this.gson.fromJson(json, rawType);
        } catch (JsonSyntaxException ex) {
            throw new UnparseableContentTypeException("CLPGJPFJ40", ex);
        }
    }

    @Override
    public <T> T fromJson(String json, Class<T> rawType, Class<?>... parameterType) {
        try {
            TypeToken<?> token = TypeToken.getParameterized(rawType, parameterType);
            Type type = token.getType();

            return this.gson.fromJson(json, type);
        } catch (JsonSyntaxException ex) {
            throw new UnparseableContentTypeException("CLPGJPFJ47", ex);
        }
    }

    @Override
    public String toJson(Object object) {
        return this.gson.toJson(object);
    }

    public static class UUIDSerializer implements JsonSerializer<UUID>, JsonDeserializer<UUID> {
        @Override
        public JsonElement serialize(UUID src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) {
                return JsonNull.INSTANCE;
            }

            return new JsonPrimitive(src.toString());
        }

        @Override
        public UUID deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json == null || json.isJsonNull()) {
                return null;
            }

            try {
                return UUID.fromString(json.getAsString());
            } catch (Exception e) {
                throw new JsonParseException(e);
            }
        }
    }

    public static class InstantSerializer implements JsonSerializer<Instant>, JsonDeserializer<Instant> {
        @Override
        public JsonElement serialize(Instant src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) {
                return JsonNull.INSTANCE;
            }

            return new JsonPrimitive(src.toString());
        }

        @Override
        public Instant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json == null || json.isJsonNull()) {
                return null;
            }

            try {
                return Instant.parse(json.getAsString());
            } catch (Exception e) {
                throw new JsonParseException(e);
            }
        }
    }
}
