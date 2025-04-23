package ddns.cli.command.profile;

import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import java.util.List;
import java.util.concurrent.Callable;

import static ddns.client.constant.Defaults.DEFAULT_DISCOVERY_INTERVAL;
import static ddns.client.constant.Defaults.DEFAULT_DISCOVERY_SOCKET_TIMEOUT;
import static ddns.client.constant.Defaults.DEFAULT_CUSTOM_PROVIDER_SOCKET_TIMEOUT;

@Command(name = "create",
        description = "Creating profile",
        mixinStandardHelpOptions = true,
        sortOptions = false,
        sortSynopsis = false)
public class ProfileCreateCommand implements Callable<Integer> {

    @ParentCommand
    private ProfileCommand profileCommand;

    @ArgGroup(heading = "Common options:\n", exclusive = false)
    private final CommonOptions commonOptions = new CommonOptions();

    @ArgGroup(heading = "Discovery options:\n", exclusive = false)
    private final DiscoveryOptions discoveryOptions = new DiscoveryOptions();

    @ArgGroup(heading = "@|bg(240),fg(230),bold,underline Custom|@ DDNS provider options:\n", exclusive = false)
    private final CustomOptions customOptions = new CustomOptions();

    static class CommonOptions {
        @Option(names = {"--name"}, required = true, description = "Unique profile name.")
        private String name;

        @Option(names = {"--active"}, description = "Is profile active.",
                defaultValue = "true", showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
        private boolean active;

        @Option(names = {"--ip-version"}, description = "IP version: 4 or 6.",
                defaultValue = "4", showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
        private byte ipVersion;
    }

    static class DiscoveryOptions {
        @Option(names = {"--discovery-interval"}, description = "IP discovery interval in @|bold seconds|@.",
                defaultValue = DEFAULT_DISCOVERY_INTERVAL + "", showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
        private int discoveryInterval;

        @Option(names = {"--discovery-method"}, description = "Discovery method: stun, dns, http.",
                defaultValue = "STUN", showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
        private String discoveryMethod;

        //TODO: remove and handle separately like http
//        @Option(names = {"--discovery-server"},
//                description = """
//                    Discovery server info. For example: "{host: stun.l.google.com, port: 19302}"
//                    If more than one server is passed, then requests will be executed in Round-robin.""")
//        private List<String> discoveryServers;

        @Option(names = {"--discovery-socket-timeout"}, description = "Discovery socket timeout in @|bold milliseconds|@.",
                defaultValue = DEFAULT_DISCOVERY_SOCKET_TIMEOUT + "", showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
        private int discoverySocketTimeout;
    }

    static class CustomOptions {
        @Option(names = {"--custom-provider"}, description = "If need custom provider.",
                defaultValue = "false", showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
        private boolean customProvider;

        @Option(names = {"--custom-url"}, description = """
            IP update URL for custom DDNS provider.
            For example: @|italic https://www.duckdns.org/update|@""")
        private String customUrl;

        @Option(names = {"--custom-socket-timeout"}, description = "IP update socket timeout in @|bold milliseconds|@.",
                defaultValue = DEFAULT_CUSTOM_PROVIDER_SOCKET_TIMEOUT + "", showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
        private int customSocketTimeout;

        @Option(names = {"--custom-method"}, description = "IP update HTTP-method for custom DDNS provider: GET or POST",
                defaultValue = "GET", showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
        private String customMethod;

        @Option(names = {"--custom-header"}, description = """
                IP update header for custom DDNS provider.
                For example: @|italic "{headerName: headerValue}"|@""")
        private List<String> customHeaders;

        @Option(names = {"--custom-param"}, description = """
                IP update query param for custom DDNS provider.
                For example: @|italic "{paramName: paramValue}"|@""")
        private List<String> customParams;
    }

    @Override
    public Integer call() throws Exception {
        System.out.println(commonOptions.name);
        System.out.println(commonOptions.active);
        System.out.println(commonOptions.ipVersion);

        System.out.println(discoveryOptions.discoveryMethod);
        System.out.println(discoveryOptions.discoveryInterval);
        System.out.println(discoveryOptions.discoverySocketTimeout);

        System.out.println(customOptions.customProvider);
        System.out.println(customOptions.customUrl);
        System.out.println(customOptions.customMethod);
        System.out.println(customOptions.customHeaders);
        System.out.println(customOptions.customParams);
        System.out.println(customOptions.customSocketTimeout);
        return 0;
    }
}
