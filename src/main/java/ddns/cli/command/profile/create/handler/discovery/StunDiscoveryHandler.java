package ddns.cli.command.profile.create.handler.discovery;

import ddns.cli.command.profile.create.ProfileCreateCommand.DiscoveryOptions;
import ddns.cli.command.profile.create.validator.TransportPortValidator;
import ddns.client.domain.DiscoveryInfo;
import ddns.client.domain.stun.StunDiscoveryInfo;
import ddns.client.domain.stun.StunServerInfo;
import picocli.CommandLine.PicocliException;

import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.TreeMap;

public class StunDiscoveryHandler {

    private final Scanner scanner = new Scanner(System.in);
    private final CommonDiscoveryHandler commonDiscoveryHandler = new CommonDiscoveryHandler();

    /**
     * key - cli position number,
     * value - stun server(host, port)
     */
    private static final Map<Integer, StunServerInfo> availableStunServers = new TreeMap<>(
            Map.of(
                    1, new StunServerInfo("stun.l.google.com", 19302),
                    2, new StunServerInfo("stun1.l.google.com", 19302),
                    3, new StunServerInfo("stun2.l.google.com", 19302),
                    4, new StunServerInfo("stun3.l.google.com", 19302),
                    5, new StunServerInfo("stun4.l.google.com", 19302)
            )
    );

    public StunDiscoveryInfo handle(DiscoveryOptions discoveryOptions) {
        System.out.println("""
                    Select one of the available stun servers by specifying its serial number:
                    """);

        for (var entry : availableStunServers.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue().getHost() + ":" + entry.getValue().getPort());
        }

        System.out.println("Or enter " + getCustomStunServerNumber() + " for pass custom STUN server.");

        try {
            int number = scanner.nextInt();
            scanner.nextLine();

            if (!availableStunServers.containsKey(number) && !Objects.equals(number, getCustomStunServerNumber())) {
                throw new PicocliException("Invalid stun server number, try again.");
            }

            var builder = StunDiscoveryInfo.builder();
            if (availableStunServers.containsKey(number)) {
                var serverInfo = availableStunServers.get(number);
                builder.server(StunServerInfo.builder()
                        .host(serverInfo.getHost())
                        .port(serverInfo.getPort())
                        .build());
            } else {
                System.out.println("Enter STUN host:");
                String host = scanner.nextLine();
                if (host.isEmpty()) {
                    throw new PicocliException("Invalid STUN host, try again.");
                }

                System.out.println("Enter STUN port:");
                int port = scanner.nextInt();
                scanner.nextLine();
                TransportPortValidator.validate(port);

                builder.server(StunServerInfo.builder()
                        .host(host)
                        .port(port)
                        .build());
            }

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

    private static int getCustomStunServerNumber() {
        int i = 0;
        for (var ignored : availableStunServers.entrySet()) {
            ++i;
        }
        return ++i;
    }
}
