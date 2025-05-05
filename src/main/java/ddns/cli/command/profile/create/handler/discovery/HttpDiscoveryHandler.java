package ddns.cli.command.profile.create.handler.discovery;

import ddns.cli.command.profile.create.ProfileCreateCommand.DiscoveryOptions;
import ddns.client.domain.DiscoveryInfo;
import ddns.client.domain.http.HttpDiscoveryInfo;
import ddns.client.domain.http.HttpMethod;
import ddns.client.domain.http.HttpServerInfo;
import ddns.client.domain.http.HttpServerInfo.ContentType;
import ddns.client.domain.http.HttpServerInfo.IPLocation;
import ddns.client.domain.http.HttpServerInfo.LocationType;
import ddns.client.domain.http.HttpServerInfo.RequestInfo;
import ddns.client.domain.http.HttpServerInfo.ResponseInfo;
import picocli.CommandLine.PicocliException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.TreeMap;

public class HttpDiscoveryHandler {

    private final Scanner scanner = new Scanner(System.in);
    private final CommonDiscoveryHandler commonDiscoveryHandler = new CommonDiscoveryHandler();

    private static final Map<Integer, ContentType> availableHttpContentTypes = new TreeMap<>(
            Map.of(
                    1, ContentType.TEXT_PLAIN,
                    2, ContentType.APPLICATION_JSON
            )
    );

    private static final Map<Integer, LocationType> availableIpLocationTypes = new TreeMap<>(
            Map.of(
                    1, LocationType.BODY,
                    2, LocationType.HEADER
            )
    );

    public HttpDiscoveryInfo handle(DiscoveryOptions discoveryOptions) {
        try {
            var builder = HttpDiscoveryInfo.builder();

            System.out.println("Enter HTTP URL:");
            String url = scanner.nextLine();
            if (url.isEmpty()) {
                throw new PicocliException("Invalid HTTP URL, try again.");
            }

            System.out.println("Enter HTTP method:");
            String method = scanner.nextLine();
            if (method.isEmpty()) {
                throw new PicocliException("Invalid HTTP method, try again.");
            }
            HttpMethod httpMethod = HttpMethod.valueOf(method.toUpperCase());

            Map<String, String> headers = fillHeaders();
            Map<String, String> parameters = fillParameters();
            ContentType responseContentType = getResponseContentType();
            LocationType ipLocationType = getIpLocation();
            String locationName = getLocationName(ipLocationType, responseContentType);

            builder.server(HttpServerInfo.builder()
                            .url(url)
                            .requestInfo(RequestInfo.builder()
                                    .method(httpMethod)
                                    .headers(headers)
                                    .parameters(parameters)
                                    .build())
                            .responseInfo(ResponseInfo.builder()
                                    .ipLocation(IPLocation.builder()
                                            .locationType(ipLocationType)
                                            .locationName(locationName)
                                            .build())
                                    .contentType(responseContentType)
                                    .build())
                    .build());

            DiscoveryInfo<?> common = commonDiscoveryHandler.handle(discoveryOptions);
            return builder
                    .discoveryInterval(common.getDiscoveryInterval())
                    .socketTimeout(common.getSocketTimeout())
                    .build();
        } catch (Exception e) {
            String msg;
            if (Objects.isNull(e.getMessage()) || e.getMessage().isEmpty()) {
                msg = "Something went wrong, try again.";
            } else {
                msg = e.getMessage();
            }
            throw new PicocliException(msg);
        }
    }

    private Map<String, String> fillHeaders() {
        Map<String, String> headers = new HashMap<>();
        System.out.println("Enter HTTP headers or press 'ENTER' to continue:");
        while (true) {
            System.out.println("Enter header name:");
            String name = scanner.nextLine();
            if (name.isEmpty()) {
                break;
            }

            System.out.println("Enter header value:");
            String value = scanner.nextLine();
            if (value.isEmpty()) {
                System.out.println("Invalid header value, try again.");
                continue;
            }

            headers.put(name, value);
            System.out.print("\n");
        }
        return headers;
    }

    private Map<String, String> fillParameters() {
        Map<String, String> parameters = new HashMap<>();
        System.out.println("Enter HTTP parameters or press 'ENTER' to continue:");
        while (true) {
            System.out.println("Enter parameter name:");
            String name = scanner.nextLine();
            if (name.isEmpty()) {
                break;
            }

            System.out.println("Enter parameter value:");
            String value = scanner.nextLine();
            if (value.isEmpty()) {
                System.out.println("Invalid parameter value, try again.");
                continue;
            }

            parameters.put(name, value);
            System.out.print("\n");
        }
        return parameters;
    }

    private ContentType getResponseContentType() {
        System.out.println("""
                    Select one of the available response content types by specifying its serial number:
                    """);

        for (var entry : availableHttpContentTypes.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        int contentTypeNumber = scanner.nextInt();
        scanner.nextLine();

        if (!availableHttpContentTypes.containsKey(contentTypeNumber)) {
            throw new PicocliException("Invalid content type number, try again.");
        }
        return availableHttpContentTypes.get(contentTypeNumber);
    }

    private LocationType getIpLocation() {
        System.out.println("""
                    Select one of the available response ip locations by specifying its serial number:
                    """);

        for (var entry : availableIpLocationTypes.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        int ipLocationNumber = scanner.nextInt();
        scanner.nextLine();

        if (!availableIpLocationTypes.containsKey(ipLocationNumber)) {
            throw new PicocliException("Invalid ip location type number, try again.");
        }
        return availableIpLocationTypes.get(ipLocationNumber);
    }

    private String getLocationName(LocationType locationType, ContentType contentType) {
        switch (locationType) {
            case BODY: {
                if (ContentType.APPLICATION_JSON.equals(contentType)) {
                    System.out.println("Enter json field name for the location type:");
                    String name = scanner.nextLine();
                    if (name.isEmpty()) {
                        throw new PicocliException("Invalid json field name for the location type");
                    }
                    return name;
                }
                break;
            } case HEADER: {
                System.out.println("Enter header name for the location type:");
                String name = scanner.nextLine();
                if (name.isEmpty()) {
                    throw new PicocliException("Invalid header name for the location type");
                }
                return name;
            }
        }
        return null;
    }
}
