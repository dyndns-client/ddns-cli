package ddns.cli.command.profile.create.handler.discovery;

import ddns.cli.command.profile.create.ProfileCreateCommand.DiscoveryOptions;
import ddns.cli.command.profile.create.validator.TransportPortValidator;
import ddns.client.domain.DiscoveryInfo;
import ddns.client.domain.dns.DnsDiscoveryInfo;
import ddns.client.domain.dns.DnsRecordType;
import ddns.client.domain.dns.DnsServerInfo;
import picocli.CommandLine.PicocliException;

import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.TreeMap;

public class DnsDiscoveryHandler {

    private final Scanner scanner = new Scanner(System.in);
    private final CommonDiscoveryHandler commonDiscoveryHandler = new CommonDiscoveryHandler();

    /**
     * key - cli position number,
     * value - dns info
     */
    private static final Map<Integer, DnsServerInfo> availableDnsServers = new TreeMap<>(
            Map.of(
                    1, new DnsServerInfo("o-o.myaddr.l.google.com", "ns1.google.com", DnsRecordType.TXT, 53)
            )
    );

    /**
     * key - cli position number,
     * value - dns record type
     */
    private static final Map<Integer, DnsRecordType> availableDnsRecordTypes = new TreeMap<>(
            Map.of(
                    1, DnsRecordType.TXT,
                    2, DnsRecordType.A,
                    3, DnsRecordType.AAAA
            )
    );

    public DnsDiscoveryInfo handle(DiscoveryOptions discoveryOptions) {
        System.out.println("""
                    Select one of the available DNS public IP lookup system by specifying its serial number:
                    """);

        for (var entry : availableDnsServers.entrySet()) {
            System.out.println(entry.getKey() + ": " + "DNS resolver: " + entry.getValue().getDnsResolverName()
                    + ", DNS domain name: " + entry.getValue().getDomainName()
                    + ", DNS record type: " + entry.getValue().getDnsRecordType()
                    + ", DNS resolver port: " + entry.getValue().getPort());
        }

        System.out.println("Or enter " + getCustomDnsServerNumber() + " for pass custom DNS info.");

        try {
            int number = scanner.nextInt();
            scanner.nextLine();

            if (!availableDnsServers.containsKey(number) && !Objects.equals(number, getCustomDnsServerNumber())) {
                throw new PicocliException("Invalid DNS server number, try again.");
            }

            var builder = DnsDiscoveryInfo.builder();
            if (availableDnsServers.containsKey(number)) {
                var serverInfo = availableDnsServers.get(number);
                builder.server(DnsServerInfo.builder()
                        .domainName(serverInfo.getDomainName())
                        .dnsResolverName(serverInfo.getDnsResolverName())
                        .dnsRecordType(serverInfo.getDnsRecordType())
                        .port(serverInfo.getPort())
                        .build());
            } else {
                System.out.println("Enter DNS resolver:");
                String host = scanner.nextLine();
                if (host.isEmpty()) {
                    throw new PicocliException("Invalid DNS resolver, try again.");
                }

                System.out.println("Enter domain name:");
                String domainName = scanner.nextLine();
                if (domainName.isEmpty()) {
                    throw new PicocliException("Invalid domain name, try again.");
                }

                System.out.println("Enter DNS port:");
                int port = scanner.nextInt();
                scanner.nextLine();
                TransportPortValidator.validate(port);

                System.out.println("Select DNS record type by specifying its serial number:");
                for (var entry : availableDnsRecordTypes.entrySet()) {
                    System.out.println(entry.getKey() + ": " + entry.getValue());
                }

                number = scanner.nextInt();
                scanner.nextLine();

                if (!availableDnsRecordTypes.containsKey(number)) {
                    throw new PicocliException("Invalid DNS record type, try again.");
                }

                builder.server(DnsServerInfo.builder()
                        .dnsResolverName(host)
                        .domainName(domainName)
                        .port(port)
                        .dnsRecordType(availableDnsRecordTypes.get(number))
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

    private static int getCustomDnsServerNumber() {
        int i = 0;
        for (var ignored : availableDnsServers.entrySet()) {
            ++i;
        }
        return ++i;
    }
}
