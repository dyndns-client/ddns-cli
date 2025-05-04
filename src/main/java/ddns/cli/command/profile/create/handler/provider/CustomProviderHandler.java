package ddns.cli.command.profile.create.handler.provider;

import ddns.client.domain.AddressUpdateInfo;
import ddns.client.domain.AddressUpdateInfo.AddressUpdateInfoBuilder;
import ddns.client.domain.http.HttpMethod;
import picocli.CommandLine;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CustomProviderHandler {

    private final Scanner scanner = new Scanner(System.in);

    public AddressUpdateInfo handle() {
        AddressUpdateInfoBuilder builder = AddressUpdateInfo.builder();

        System.out.println("Enter IP update HTTP URL:");
        String url = scanner.nextLine();
        if (url.isEmpty()) {
            throw new CommandLine.PicocliException("Invalid HTTP URL, try again.");
        }
        builder.url(url);

        System.out.println("Enter HTTP method:");
        String method = scanner.nextLine();
        if (method.isEmpty()) {
            throw new CommandLine.PicocliException("Invalid HTTP method, try again.");
        }
        builder.method(HttpMethod.valueOf(method.toUpperCase()));

        var headers = fillHeaders();
        builder.headers(headers);

        var parameters = fillParameters();
        builder.parameters(parameters);

        builder.socketTimeout(10_000);

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
}
