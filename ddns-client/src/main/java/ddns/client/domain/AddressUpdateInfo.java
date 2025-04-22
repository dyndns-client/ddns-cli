package ddns.client.domain;

import ddns.client.domain.http.HttpMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AddressUpdateInfo {
    private String url;
    private HttpMethod method;
    @Builder.Default
    private Map<String, String> headers = new HashMap<>();
    @Builder.Default
    // GET query params or POST application/x-www-form-urlencoded params
    private Map<String, String> parameters = new HashMap<>();
}
