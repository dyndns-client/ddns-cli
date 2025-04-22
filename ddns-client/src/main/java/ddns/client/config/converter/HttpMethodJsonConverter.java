package ddns.client.config.converter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ddns.client.domain.http.HttpMethod;

import java.lang.reflect.Type;

public class HttpMethodJsonConverter implements JsonSerializer<HttpMethod>, JsonDeserializer<HttpMethod> {

    @Override
    public JsonElement serialize(HttpMethod src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.name());
    }

    @Override
    public HttpMethod deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String element = json.getAsString();
        try {
            return HttpMethod.valueOf(element.toUpperCase());
        } catch (Exception e) {
            throw new JsonParseException(e);
        }
    }
}
