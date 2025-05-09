package ddns.client.updater;

import ddns.client.domain.AddressUpdateInfo;
import ddns.client.domain.IP;
import ddns.client.domain.Profile;
import ddns.client.domain.http.HttpMethod;
import ddns.client.domain.http.IPLocation;
import ddns.client.profile.ProfileLogger;
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
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.util.Timeout;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static ddns.client.domain.http.LocationType.BODY;
import static ddns.client.domain.http.LocationType.HEADER;

@RequiredArgsConstructor
public class IpUpdater {

    private final String basePath;

    public boolean update(IP ip, Profile profile) {
        AddressUpdateInfo updateInfo = profile.getUpdateInfo();

        String url = updateInfo.getUrl();

        // -- Request info --
        HttpMethod method = updateInfo.getMethod();
        Map<String, String> parameters = updateInfo.getParameters();
        Map<String, String> headers = updateInfo.getHeaders();

        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setSocketTimeout(Timeout.ofMilliseconds(updateInfo.getSocketTimeout()))
                .build();

        try (BasicHttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager();
             CloseableHttpClient client = HttpClients.custom().setConnectionManager(connectionManager).build()) {
            connectionManager.setConnectionConfig(connectionConfig);

            ClassicHttpRequest request = null;
            URIBuilder uriBuilder = new URIBuilder()
                    .setHttpHost(HttpHost.create(new URI(url)));

            IPLocation ipLocation = updateInfo.getIpLocation();

            if (Objects.isNull(parameters)) {
                parameters = new HashMap<>();
            } else {
                parameters = new HashMap<>(parameters);
            }
            if (BODY.equals(ipLocation.getLocationType())) {
                parameters.put(ipLocation.getLocationName(), ip.getValue());
            }

            if (Objects.isNull(headers)) {
                headers = new HashMap<>();
            } else {
                headers = new HashMap<>(headers);
            }
            if (HEADER.equals(ipLocation.getLocationType())) {
                headers.put(ipLocation.getLocationName(), ip.getValue());
            }

            if (HttpMethod.GET.equals(method)) {
                if (!parameters.isEmpty()) {
                    if (BODY.equals(ipLocation.getLocationType())) {
                        parameters.put(ipLocation.getLocationName(), ip.getValue());
                    }
                    for (Map.Entry<String, String> entry : parameters.entrySet()) {
                        uriBuilder.addParameter(entry.getKey(), entry.getValue());
                    }
                }
                request = new HttpGet(uriBuilder.build());
            } else if (HttpMethod.POST.equals(method)) {
                request = new HttpPost(uriBuilder.build());

                if (!parameters.isEmpty()) {
                    if (BODY.equals(ipLocation.getLocationType())) {
                        parameters.put(ipLocation.getLocationName(), ip.getValue());
                    }
                    List<NameValuePair> params = new ArrayList<>();

                    for (Map.Entry<String, String> entry : parameters.entrySet()) {
                        params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                    }
                    request.setEntity(new UrlEncodedFormEntity(params));
                }
            }

            try (CloseableHttpResponse response = client.execute(request)) {
                int code = response.getCode();
                if (code != 200) {
                    ProfileLogger.error(basePath, profile.getName(), "Error occurred while IP update: " + ip);
                }
            } catch (IOException e) {
                ProfileLogger.error(basePath, profile.getName(), "Error occurred while IP update: " + ip + ". Reason: " + e.getMessage());
                return false;
            }
        } catch (IOException | URISyntaxException e) {
            ProfileLogger.error(basePath, profile.getName(), "Error occurred while IP update: " + ip + ". Reason: " + e.getMessage());
            return false;
        }

        return true;
    }
}
