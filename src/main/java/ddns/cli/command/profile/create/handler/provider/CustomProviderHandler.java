package ddns.cli.command.profile.create.handler.provider;

import ddns.client.domain.AddressUpdateInfo;
import ddns.client.domain.AddressUpdateInfo.AddressUpdateInfoBuilder;
import ddns.client.domain.http.HttpMethod;
import ddns.client.domain.http.IPLocation;
import ddns.client.domain.http.LocationType;
import picocli.CommandLine;
import picocli.CommandLine.PicocliException;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class CustomProviderHandler {

    private final Scanner scanner = new Scanner(System.in);

    private static final Map<Integer, LocationType> availableLocationTypes = new TreeMap<>(
            Map.of(
                    1, LocationType.BODY,
                    2, LocationType.HEADER
            )
    );

    public AddressUpdateInfo handle() {
        AddressUpdateInfoBuilder builder = AddressUpdateInfo.builder();

        System.out.println("Enter IP update HTTP URL:");
        String url = scanner.nextLine();
        if (url.isEmpty()) {
            throw new PicocliException("Invalid HTTP URL, try again.");
        }
        builder.url(url);

        System.out.println("Enter HTTP method:");
        String method = scanner.nextLine();
        if (method.isEmpty()) {
            throw new PicocliException("Invalid HTTP method, try again.");
        }
        builder.method(HttpMethod.valueOf(method.toUpperCase()));

        var headers = fillHeaders();
        builder.headers(headers);

        var parameters = fillParameters();
        builder.parameters(parameters);

        builder.socketTimeout(10_000);

        System.out.println("""
                Select one of the available IP location type when requesting IP update by specifying its serial number:
                """);

        for (var entry : availableLocationTypes.entrySet()) {
            LocationType value = entry.getValue();
            if (LocationType.BODY.equals(value)) {
                System.out.println(entry.getKey() + ": " + entry.getValue() + ": " + "GET-parameter or POST-application/x-www-form-urlencoded");
            } else {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }
        int number = scanner.nextInt();
        scanner.nextLine();

        if (!availableLocationTypes.containsKey(number)) {
            throw new PicocliException("Invalid IP location type number, try again.");
        }

        LocationType locationType = availableLocationTypes.get(number);
        if (locationType.equals(LocationType.BODY)) {
            System.out.println("Enter IP parameter name for update:");
            String name = scanner.nextLine();
            builder.ipLocation(IPLocation.builder()
                    .locationName(name)
                    .locationType(LocationType.BODY)
                    .build());
        } else {
            System.out.println("Enter IP header name for update:");
            String name = scanner.nextLine();
            builder.ipLocation(IPLocation.builder()
                    .locationName(name)
                    .locationType(LocationType.HEADER)
                    .build());
        }

        return builder.build();
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
                System.out.println(
                        CommandLine.Help.Ansi.AUTO.text("@|bold Invalid header value, try again.|@")
                );
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
                System.out.println(
                        CommandLine.Help.Ansi.AUTO.text("@|bold Invalid parameter value, try again.|@")
                );
                continue;
            }

            parameters.put(name, value);
            System.out.print("\n");
        }
        return parameters;
    }
}
