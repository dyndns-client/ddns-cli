package ddns.client.config.converter;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import ddns.client.domain.http.HttpServerInfo.ContentType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContentTypeJsonConverterTest {

    ContentTypeJsonConverter converter = new ContentTypeJsonConverter();

    @Test
    void serialize_application_json() {
        JsonElement serialize = converter.serialize(ContentType.APPLICATION_JSON, null, null);
        assertNotNull(serialize);
        assertEquals("application/json", serialize.getAsString());
    }

    @Test
    void serialize_text_plain() {
        JsonElement serialize = converter.serialize(ContentType.TEXT_PLAIN, null, null);
        assertNotNull(serialize);
        assertEquals("text/plain", serialize.getAsString());
    }

    @Test
    void deserialize_application_json() {
        ContentType deserialize = converter.deserialize(new JsonPrimitive("application/json"), null, null);
        assertNotNull(deserialize);
        assertEquals(ContentType.APPLICATION_JSON, deserialize);
    }

    @Test
    void deserialize_text_plain() {
        ContentType deserialize = converter.deserialize(new JsonPrimitive("text/plain"), null, null);
        assertNotNull(deserialize);
        assertEquals(ContentType.TEXT_PLAIN, deserialize);
    }

    @Test
    void deserialize_unknown() {
        assertThrows(JsonParseException.class, () -> converter.deserialize(new JsonPrimitive("unknown"), null, null));
    }
}
