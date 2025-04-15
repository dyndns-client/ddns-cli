package ddns.client.domain.http;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class HttpServerInfo {
    private String url;
    private RequestInfo requestInfo;
    private ResponseInfo responseInfo;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class RequestInfo {
        private HttpMethod method;
        private Map<String, String> parameters;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class ResponseInfo {
        private ContentType contentType;
        private IPLocation ipLocation;
    }

    public static class IPLocation {
        private LocationType locationType;
        private String name;
    }

    public enum HttpMethod {
        GET,
        POST
    }

    @RequiredArgsConstructor
    @Getter
    public enum ContentType {
        TEXT_PLAIN("text/plain"),
        APPLICATION_JSON("application/json");

        private final String httpValue;
    }

    public enum LocationType {
        BODY,
        HEADER
    }
}
