package ddns.cli.command.profile.create.handler.provider;

import ddns.client.domain.AddressUpdateInfo;
import ddns.client.domain.http.HttpMethod;
import ddns.client.domain.http.IPLocation;
import ddns.client.domain.http.LocationType;
import org.apache.commons.codec.binary.Base64;
import picocli.CommandLine.PicocliException;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class NoipHandler {

    private final Scanner scanner = new Scanner(System.in);

    public AddressUpdateInfo handle() {
        System.out.println("Enter username:");
        String username = scanner.nextLine();
        if (username.isEmpty()) {
            throw new PicocliException("Invalid username, try again.");
        }

        System.out.println("Enter password:");
        String password = scanner.nextLine();
        if (password.isEmpty()) {
            throw new PicocliException("Invalid password, try again.");
        }

        System.out.println("Enter hostname:");
        String hostname = scanner.nextLine();
        if (hostname.isEmpty()) {
            throw new PicocliException("Invalid hostname, try again.");
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", Base64.encodeBase64String((username + ":" + password).getBytes()));

        return AddressUpdateInfo.builder()
                .url("https://dynupdate.no-ip.com/nic/update")
                .method(HttpMethod.GET)
                .socketTimeout(10_000)
                .parameters(Map.of("hostname", hostname))
                .headers(headers)
                .ipLocation(IPLocation.builder()
                        .locationType(LocationType.BODY)
                        .locationName("myip")
                        .build())
                .build();
    }
}
