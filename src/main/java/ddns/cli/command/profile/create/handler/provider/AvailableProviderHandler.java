package ddns.cli.command.profile.create.handler.provider;

import ddns.client.domain.AddressUpdateInfo;
import ddns.client.domain.AvailableProvider;
import picocli.CommandLine.PicocliException;

import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class AvailableProviderHandler {

    private final Scanner scanner = new Scanner(System.in);
    private final DynuHandler dynuHandler = new DynuHandler();
    private final DuckDnsHandler duckDnsHandler = new DuckDnsHandler();
    private final FreeDnsHandler freeDnsHandler = new FreeDnsHandler();
    private final NoipHandler noipHandler = new NoipHandler();

    private static final Map<Integer, AvailableProvider> availableProviders = new TreeMap<>(
            Map.of(
                    1, AvailableProvider.DYNU,
                    2, AvailableProvider.NOIP,
                    3, AvailableProvider.DuckDNS,
                    4, AvailableProvider.FreeDNS
            )
    );

    public AddressUpdateInfo handle() {
        System.out.println("""
                    Select one of the available ddns providers by specifying its serial number:
                    """);

        for (var entry : availableProviders.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue().getLink());
        }

        int number = scanner.nextInt();
        scanner.nextLine();

        if (!availableProviders.containsKey(number)) {
            throw new PicocliException("Invalid provider number, try again.");
        }

        AvailableProvider availableProvider = availableProviders.get(number);
        return switch (availableProvider) {
            case DYNU -> dynuHandler.handle();
            case DuckDNS -> duckDnsHandler.handle();
            case FreeDNS -> freeDnsHandler.handle();
            case NOIP -> noipHandler.handle();
        };
    }
}
