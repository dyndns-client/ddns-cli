package ddns.webapi.handler;

import com.google.gson.Gson;
import ddns.client.profile.ProfileClient;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.HttpRequestHandler;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.net.URIBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ProfileLogsHandler implements HttpRequestHandler {

    private final ProfileClient profileClient;
    private final String basePath;

    @SneakyThrows
    @Override
    public void handle(ClassicHttpRequest request, ClassicHttpResponse response, HttpContext httpContext) throws HttpException, IOException {
        URI uri = request.getUri();
        Map<String, String> params = new URIBuilder(uri).getQueryParams().stream()
                .collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));

        if (!params.containsKey("profileName")) {
            response.setCode(404);
            return;
        }

        List<String> logs = profileClient.getProfileLogs(params.get("profileName"), basePath);
        String json = new Gson().toJson(logs);
        response.setEntity(new StringEntity(json));
    }
}
