package ddns.webapi.handler;

import ddns.client.profile.ProfileClient;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.HttpRequestHandler;
import org.apache.hc.core5.http.protocol.HttpContext;

import java.io.IOException;

@RequiredArgsConstructor
public class ProfileRemoveHandler implements HttpRequestHandler {

    private final ProfileClient profileClient;

    @Override
    public void handle(ClassicHttpRequest request, ClassicHttpResponse response, HttpContext httpContext) throws HttpException, IOException {
//        profileClient.removeProfile()
        response.setCode(200);
    }
}
