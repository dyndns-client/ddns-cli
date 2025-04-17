package ddns.client.discovery;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ddns.client.domain.IP;
import ddns.client.domain.http.HttpServerInfo;
import ddns.client.domain.http.HttpServerInfo.ContentType;
import ddns.client.domain.http.HttpServerInfo.HttpMethod;
import ddns.client.domain.http.HttpServerInfo.LocationType;
import ddns.client.exception.DiscoveryException;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.BasicHttpClientConnectionManager;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.util.Timeout;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class HttpDiscoveryService {

    private final Gson gson;

    public IP discover(HttpServerInfo httpServerInfo, int socketTimeout) throws DiscoveryException {
        String url = httpServerInfo.getUrl();

        // -- Request info --
        HttpMethod method = httpServerInfo.getRequestInfo().getMethod();
        Map<String, String> parameters = httpServerInfo.getRequestInfo().getParameters();

        // -- Response info --
        ContentType contentType = httpServerInfo.getResponseInfo().getContentType();
        LocationType locationType = httpServerInfo.getResponseInfo().getIpLocation().getLocationType();
        String locationName = httpServerInfo.getResponseInfo().getIpLocation().getLocationName();

        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setSocketTimeout(Timeout.ofMilliseconds(socketTimeout))
                .build();

        try (BasicHttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager();
             CloseableHttpClient client = HttpClients.custom().setConnectionManager(connectionManager).build()) {
            connectionManager.setConnectionConfig(connectionConfig);

            ClassicHttpRequest request = null;
            URIBuilder uriBuilder = new URIBuilder()
                    .setHttpHost(HttpHost.create(new URI(url)));

            if (HttpMethod.GET.equals(method)) {
                if (Objects.nonNull(parameters) && !parameters.isEmpty()) {
                    for (Map.Entry<String, String> entry : parameters.entrySet()) {
                        uriBuilder.addParameter(entry.getKey(), entry.getValue());
                    }
                }
                request = new HttpGet(uriBuilder.build());
            } else if (HttpMethod.POST.equals(method)) {
                request = new HttpPost(uriBuilder.build());

                if (Objects.nonNull(parameters) && !parameters.isEmpty()) {
                    List<NameValuePair> params = new ArrayList<>();

                    for (Map.Entry<String, String> entry : parameters.entrySet()) {
                        params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                    }
                    request.setEntity(new UrlEncodedFormEntity(params));
                }
            }

            if (Objects.isNull(request)) {
                throw new DiscoveryException("Http method " + method + " not supported");
            }

            try (CloseableHttpResponse response = client.execute(request)) {
                if (LocationType.HEADER.equals(locationType)) {
                    Header header = response.getHeader(locationName);

                    if (Objects.isNull(header)) {
                        throw new DiscoveryException("Header not found: " + locationName);
                    }
                    String value = header.getValue();
                    value = value.trim();
                    return new IP(value);
                }

                if (LocationType.BODY.equals(locationType)) {
                    String body = EntityUtils.toString(response.getEntity());
                    Header contentTypeHeader = response.getHeader("Content-Type");

                    if (ContentType.APPLICATION_JSON.equals(contentType)
                            && contentType.getHttpValue().equals(contentTypeHeader.getValue())) {
                        Type type = new TypeToken<Map<String, String>>() {}.getType();
                        Map<String, String> map = gson.fromJson(body, type);
                        String value = map.get(locationName);

                        if (Objects.isNull(value)) {
                            throw new DiscoveryException("Public IP value not found in response JSON!");
                        }
                        value = value.trim();
                        return new IP(value);
                    } else if (ContentType.TEXT_PLAIN.equals(contentType)
                            && contentType.getHttpValue().equals(contentTypeHeader.getValue())) {
                        if (Objects.isNull(body)) {
                            throw new DiscoveryException("Public IP value not found in response body!");
                        }
                        body = body.trim();
                        return new IP(body);
                    }
                }
            }
        } catch (IOException | URISyntaxException | ProtocolException e) {
            throw new DiscoveryException(e.getMessage());
        }

        throw new DiscoveryException("Public IP not found!");
    }
}
