package ddns.cli.command.profile.create.handler.provider;

import ddns.client.domain.AddressUpdateInfo;
import ddns.client.domain.AddressUpdateInfo.AddressUpdateInfoBuilder;
import ddns.client.domain.http.HttpMethod;
import ddns.client.domain.http.IPLocation;
import ddns.client.domain.http.LocationType;
import org.apache.commons.codec.digest.DigestUtils;
import picocli.CommandLine.PicocliException;

import java.util.Map;
import java.util.Scanner;

public class FreeDnsHandler {

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

        byte[] sha1 = DigestUtils.sha1(username + "|" + password);

        System.out.println("Enter host:");
        String host = scanner.nextLine();
        if (host.isEmpty()) {
            throw new PicocliException("Invalid host, try again.");
        }

        AddressUpdateInfoBuilder builder = AddressUpdateInfo.builder();
        builder.url("https://freedns.afraid.org/dynamic/update.php" + "?" + new String(sha1));
        builder.method(HttpMethod.GET);
        builder.socketTimeout(10_000);
        builder.parameters(Map.of("host", host));
        builder.ipLocation(IPLocation.builder()
                .locationName("address")
                .locationType(LocationType.BODY)
                .build());
        return builder.build();
    }
}
