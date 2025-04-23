package ddns.client.domain.http;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
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
        @Builder.Default
        private Map<String, String> headers = new HashMap<>();
        // GET query params or POST application/x-www-form-urlencoded params
        @Builder.Default
        private Map<String, String> parameters = new HashMap<>();
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class ResponseInfo {
        @Builder.Default
        private ContentType contentType = ContentType.TEXT_PLAIN;
        private IPLocation ipLocation;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class IPLocation {
        @Builder.Default
        private LocationType locationType = LocationType.BODY;
        /**
         * If locationType is 'HEADER' or locationType is 'BODY' and contentType is 'application/json'
         * Then locationName is header-name or field-name
         */
        private String locationName;
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
