package ddns.cli.command.profile.create.handler.discovery;

import ddns.cli.command.profile.create.ProfileCreateCommand.DiscoveryOptions;
import ddns.client.constant.Defaults;
import ddns.client.domain.DiscoveryInfo;

import java.util.Scanner;

public class CommonDiscoveryHandler {

    private final Scanner scanner = new Scanner(System.in);

    public DiscoveryInfo<?> handle(DiscoveryOptions discoveryOptions) {
        DiscoveryInfo<?> discoveryInfo = new DiscoveryInfo<>() {
        };

        if (discoveryOptions.getDiscoveryInterval() < 0) {
            System.out.println(
                    "Enter discover interval in seconds, or press ENTER for use default: "
                            + Defaults.DEFAULT_DISCOVERY_INTERVAL);
            String line = scanner.nextLine();
            int discoveryInterval = Defaults.DEFAULT_DISCOVERY_INTERVAL;
            if (!line.isEmpty()) {
                discoveryInterval = Integer.parseInt(line);
            }
            discoveryInfo.setDiscoveryInterval(discoveryInterval);
        } else {
            discoveryInfo.setDiscoveryInterval(discoveryOptions.getDiscoveryInterval());
        }

        if (discoveryOptions.getDiscoverySocketTimeout() < 0) {
            System.out.println(
                    "Enter discover socket timeout in milliseconds, or press ENTER for use default: "
                            + Defaults.DEFAULT_DISCOVERY_SOCKET_TIMEOUT);
            String line = scanner.nextLine();
            int socketTimeout = Defaults.DEFAULT_DISCOVERY_SOCKET_TIMEOUT;
            if (!line.isEmpty()) {
                socketTimeout = Integer.parseInt(line);
            }

            discoveryInfo.setSocketTimeout(socketTimeout);
        } else {
            discoveryInfo.setSocketTimeout(discoveryOptions.getDiscoverySocketTimeout());
        }

        return discoveryInfo;
    }
}
