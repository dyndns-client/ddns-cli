package ddns.webapi.handler;

import ddns.client.daemon.DaemonClient;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.HttpRequestHandler;
import org.apache.hc.core5.http.protocol.HttpContext;

import java.io.IOException;
import java.net.URISyntaxException;

@RequiredArgsConstructor
public class ProfileRemoveHandler extends HandlerBase implements HttpRequestHandler {

    private final DaemonClient daemonClient;
    private final String basePath;

    @Override
    public void handle(ClassicHttpRequest request, ClassicHttpResponse response, HttpContext httpContext) throws HttpException, IOException {
        if (!isSessionIdValid(request, response)) {
            return;
        }

        String profileName;
        try {
            profileName = getProfileName(request.getUri().toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        daemonClient.removeProfileFromWork(profileName, basePath);
        response.setCode(200);
    }

    private String getProfileName(String formData) {
        String[] pairs = formData.split("\\?");
        String[] split = pairs[1].split("=");
        return split[1];
    }
}
