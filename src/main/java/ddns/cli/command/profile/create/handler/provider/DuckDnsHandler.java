package ddns.cli.command.profile.create.handler.provider;

import ddns.client.domain.AddressUpdateInfo;
import ddns.client.domain.AddressUpdateInfo.AddressUpdateInfoBuilder;
import ddns.client.domain.http.HttpMethod;
import picocli.CommandLine.PicocliException;

import java.util.Map;
import java.util.Scanner;

public class DuckDnsHandler {

    private final Scanner scanner = new Scanner(System.in);

    public AddressUpdateInfo handle() {
        System.out.println("Enter 'domains' query parameter:");
        String domains = scanner.nextLine();
        if (domains.isEmpty()) {
            throw new PicocliException("Domains cannot be empty, try again.");
        }

        System.out.println("Enter 'token' query parameter:");
        String token = scanner.nextLine();
        if (token.isEmpty()) {
            throw new PicocliException("Token cannot be empty, try again.");
        }

        AddressUpdateInfoBuilder builder = AddressUpdateInfo.builder();
        builder.url("https://www.duckdns.org/update");
        builder.method(HttpMethod.GET);
        builder.socketTimeout(10_000);
        builder.parameters(Map.of("domains", domains, "token", token));
        return builder.build();
    }
}
