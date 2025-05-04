package ddns.client.config.converter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ddns.client.domain.http.HttpServerInfo.ContentType;

import java.lang.reflect.Type;

public class ContentTypeJsonConverter implements JsonSerializer<ContentType>, JsonDeserializer<ContentType> {

    @Override
    public JsonElement serialize(ContentType src, Type typeOfSrc, JsonSerializationContext context) {
        String element = src.getHttpValue();
        return new JsonPrimitive(element);
    }

    @Override
    public ContentType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String element = json.getAsString();
        try {
            ContentType[] values = ContentType.values();
            for (ContentType value : values) {
                if (value.getHttpValue().equals(element)) {
                    return value;
                }
            }
        } catch (Exception e) {
            throw new JsonParseException(e);
        }
        throw new JsonParseException("Unknown content type: " + element);
    }
}
