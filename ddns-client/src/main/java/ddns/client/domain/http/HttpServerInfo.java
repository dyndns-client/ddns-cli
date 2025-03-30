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
    private HttpMethod method;
    private RequestInfo requestInfo;
    private ResponseInfo responseInfo;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class RequestInfo {
        private Map<String, String> parameters;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class ResponseInfo {
        private String contentType;
        private IPLocation ipLocation;
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

    public enum IPLocation {
        BODY,
        HEADER
    }
}
