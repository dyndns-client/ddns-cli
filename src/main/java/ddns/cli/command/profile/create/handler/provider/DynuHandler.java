package ddns.cli.command.profile.create.handler.provider;

import ddns.client.domain.AddressUpdateInfo;
import ddns.client.domain.http.HttpMethod;
import picocli.CommandLine.PicocliException;

import java.util.Map;
import java.util.Scanner;

public class DynuHandler {

    private final Scanner scanner = new Scanner(System.in);

    public AddressUpdateInfo handle() {
        System.out.println("Enter hostname:");
        String hostname = scanner.nextLine();
        if (hostname.isEmpty()) {
            throw new PicocliException("Hostname cannot be empty, try again.");
        }

        System.out.println("Enter username:");
        String username = scanner.nextLine();
        if (username.isEmpty()) {
            throw new PicocliException("Username cannot be empty, try again.");
        }

        System.out.println("Enter password:");
        String password = scanner.nextLine();
        if (password.isEmpty()) {
            throw new PicocliException("Password cannot be empty, try again.");
        }

        return AddressUpdateInfo.builder()
                .url("https://api.dynu.com/nic/update")
                .socketTimeout(10_000)
                .method(HttpMethod.GET)
                .parameters(Map.of("hostname", hostname))
                .build();
    }
}
