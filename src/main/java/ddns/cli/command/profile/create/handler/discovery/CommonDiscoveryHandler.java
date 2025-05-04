package ddns.cli.command.profile.create.handler.discovery;

import ddns.client.constant.Defaults;
import ddns.client.domain.DiscoveryInfo;

import java.util.Scanner;

public class CommonDiscoveryHandler {

    private final Scanner scanner = new Scanner(System.in);

    public DiscoveryInfo<?> handle() {
        System.out.println(
                "Enter discover interval in seconds, or press ENTER for use default: "
                        + Defaults.DEFAULT_DISCOVERY_INTERVAL);
        String line = scanner.nextLine();
        int discoveryInterval = Defaults.DEFAULT_DISCOVERY_INTERVAL;
        if (!line.isEmpty()) {
            discoveryInterval = Integer.parseInt(line);
        }

        System.out.println(
                "Enter discover socket timeout in milliseconds, or press ENTER for use default: "
                        + Defaults.DEFAULT_DISCOVERY_SOCKET_TIMEOUT);
        line = scanner.nextLine();
        int socketTimeout = Defaults.DEFAULT_DISCOVERY_SOCKET_TIMEOUT;
        if (!line.isEmpty()) {
            socketTimeout = Integer.parseInt(line);
        }

        DiscoveryInfo<?> discoveryInfo = new DiscoveryInfo<>() {
        };
        discoveryInfo.setDiscoveryInterval(discoveryInterval);
        discoveryInfo.setSocketTimeout(socketTimeout);

        return discoveryInfo;
    }
}
