package ddns.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dagger.Module;
import dagger.Provides;
import ddns.client.config.converter.ContentTypeJsonConverter;
import ddns.client.domain.http.HttpServerInfo;

@Module
public class GsonConfig {

    @Provides
    public Gson provideGson() {
        return new GsonBuilder()
                .registerTypeAdapter(HttpServerInfo.ContentType.class, new ContentTypeJsonConverter())
                .setPrettyPrinting()
                .create();
    }
}
