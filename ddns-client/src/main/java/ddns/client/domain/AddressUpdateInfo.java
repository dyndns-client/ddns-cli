package ddns.client.domain;

import ddns.client.domain.http.HttpMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import static ddns.client.constant.Defaults.DEFAULT_CUSTOM_PROVIDER_SOCKET_TIMEOUT;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AddressUpdateInfo {
    private String url;
    private HttpMethod method;
    @Builder.Default
    private int socketTimeout = DEFAULT_CUSTOM_PROVIDER_SOCKET_TIMEOUT;
    @Builder.Default
    private Map<String, String> headers = new HashMap<>();
    // GET query params or POST application/x-www-form-urlencoded params
    @Builder.Default
    private Map<String, String> parameters = new HashMap<>();
}
