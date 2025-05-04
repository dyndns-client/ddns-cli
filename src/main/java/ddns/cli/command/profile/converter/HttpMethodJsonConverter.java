package ddns.cli.command.profile.converter;

import com.google.gson.Gson;
import ddns.client.domain.http.HttpMethod;
import picocli.CommandLine;

public class HttpMethodJsonConverter implements CommandLine.ITypeConverter<HttpMethod> {

    private final Gson gson = new Gson();

    @Override
    public HttpMethod convert(String value) {
        return gson.fromJson(value, HttpMethod.class);
    }
}
