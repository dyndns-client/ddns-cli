package ddns.cli.command.profile.create;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ddns.cli.command.profile.ProfileCommand;
import ddns.cli.command.profile.converter.DiscoveryMethodJsonConverter;
import ddns.cli.command.profile.converter.IPVersionJsonConverter;
import ddns.cli.command.profile.create.handler.discovery.DnsDiscoveryHandler;
import ddns.cli.command.profile.create.handler.discovery.HttpDiscoveryHandler;
import ddns.cli.command.profile.create.handler.discovery.StunDiscoveryHandler;
import ddns.cli.command.profile.create.handler.provider.AvailableProviderHandler;
import ddns.cli.command.profile.create.handler.provider.CustomProviderHandler;
import ddns.cli.command.profile.create.validator.ProfileCreateValidator;
import ddns.client.domain.AddressUpdateInfo;
import ddns.client.domain.DiscoveryMethod;
import ddns.client.domain.IPVersion;
import ddns.client.domain.Profile;
import ddns.client.domain.Profile.ProfileBuilder;
import ddns.client.domain.dns.DnsDiscoveryInfo;
import ddns.client.domain.http.HttpDiscoveryInfo;
import ddns.client.domain.stun.StunDiscoveryInfo;
import lombok.Data;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;
import picocli.CommandLine.PicocliException;

import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.Callable;

import static ddns.client.constant.Defaults.DEFAULT_DISCOVERY_INTERVAL;
import static ddns.client.constant.Defaults.DEFAULT_DISCOVERY_SOCKET_TIMEOUT;
import static picocli.CommandLine.ExitCode.OK;

@Command(name = "create",
        description = """
                Creating profile.
                @|underline Some ddns options will be prompted to be entered manually.|@
                """,
        mixinStandardHelpOptions = true,
        sortOptions = false,
        sortSynopsis = false)
public class ProfileCreateCommand implements Callable<Integer> {

    private final StunDiscoveryHandler stunHandler = new StunDiscoveryHandler();
    private final DnsDiscoveryHandler dnsHandler = new DnsDiscoveryHandler();
    private final HttpDiscoveryHandler httpHandler = new HttpDiscoveryHandler();
    private final AvailableProviderHandler availableProviderHandler = new AvailableProviderHandler();
    private final CustomProviderHandler customProviderHandler = new CustomProviderHandler();

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private String basePath;

    @ParentCommand
    private ProfileCommand profileCommand;

    @ArgGroup(heading = "Common options:%n", exclusive = false)
    private final CommonOptions commonOptions = new CommonOptions();

    @ArgGroup(heading = "Discovery options:%n", exclusive = false)
    private final DiscoveryOptions discoveryOptions = new DiscoveryOptions();

    @ArgGroup(heading = "@|bg(240),fg(230),bold,underline Custom|@ DDNS provider options:%n", exclusive = false)
    private final CustomOptions customOptions = new CustomOptions();

    @Data
    public static class CommonOptions {
        @Option(names = {"--name"}, defaultValue = "${env:name}", required = true, description = "Unique profile name.")
        private String name;

        @Option(names = {"--active"}, description = "Is profile active.",
                defaultValue = "true", showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
        private boolean active;

        @Option(names = {"--ip-version"}, description = "IP version: 4 or 6.",
                defaultValue = "4", showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
                converter = IPVersionJsonConverter.class)
        private IPVersion ipVersion;
    }

    @Data
    public static class DiscoveryOptions {
        @Option(names = {"--discovery-interval"}, description = "Discovery interval in @|bold seconds|@.",
                defaultValue = DEFAULT_DISCOVERY_INTERVAL + "", showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
        private int discoveryInterval;

        @Option(names = {"--discovery-method"}, description = "Discovery method: STUN, DNS, HTTP.",
                defaultValue = "STUN", showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
                converter = DiscoveryMethodJsonConverter.class)
        private DiscoveryMethod discoveryMethod;

        @Option(names = {"--discovery-socket-timeout"}, description = "Discovery socket timeout in @|bold milliseconds|@.",
                defaultValue = DEFAULT_DISCOVERY_SOCKET_TIMEOUT + "", showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
        private int discoverySocketTimeout;
    }

    @Data
    public static class CustomOptions {
        @Option(names = {"--custom-provider"}, description = "If a custom provider need.",
                defaultValue = "false", showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
        private boolean customProvider;
    }

    @Override
    public Integer call() {
        ProfileCreateValidator.validate(commonOptions, discoveryOptions, customOptions);

        if (Objects.isNull(commonOptions.name)) {
            commonOptions.setName(getProfileName());
        }

        ProfileBuilder builder = Profile.builder();

        switch (discoveryOptions.getDiscoveryMethod()) {
            case STUN: {
                StunDiscoveryInfo stunDiscoveryInfo = stunHandler.handle();
                System.out.println(
                        CommandLine.Help.Ansi.AUTO.text(String.format("@|underline,bg(60),fg(46) STUN discovery info successfully created:|@ %s\n", gson.toJson(stunDiscoveryInfo)))
                );
                builder.discoveryMethod(DiscoveryMethod.STUN);
                builder.stunDiscoveryInfo(stunDiscoveryInfo);
                break;
            }
            case DNS: {
                DnsDiscoveryInfo dnsDiscoveryInfo = dnsHandler.handle();
                System.out.println(
                        CommandLine.Help.Ansi.AUTO.text(String.format("@|underline,bg(60),fg(46) DNS discovery info successfully created:|@ %s\n", gson.toJson(dnsDiscoveryInfo)))
                );
                builder.discoveryMethod(DiscoveryMethod.DNS);
                builder.dnsDiscoveryInfo(dnsDiscoveryInfo);
                break;
            }
            case HTTP: {
                HttpDiscoveryInfo httpDiscoveryInfo = httpHandler.handle();
                System.out.println(
                        CommandLine.Help.Ansi.AUTO.text(String.format("@|underline,bg(60),fg(46) HTTP discovery info successfully created:|@ %s\n", gson.toJson(httpDiscoveryInfo)))
                );
                builder.discoveryMethod(DiscoveryMethod.HTTP);
                builder.httpDiscoveryInfo(httpDiscoveryInfo);
                break;
            }
        }

        AddressUpdateInfo addressUpdateInfo;
        if (customOptions.isCustomProvider()) {
            addressUpdateInfo = customProviderHandler.handle();
        } else {
            addressUpdateInfo = availableProviderHandler.handle();
        }

        builder.updateInfo(addressUpdateInfo);
        builder.active(commonOptions.active);
        Profile profile = builder.build();


        System.out.println(
                CommandLine.Help.Ansi.AUTO.text(String.format("@|underline,bg(60),fg(46) Profile successfully created:|@ %s\n", gson.toJson(profile)))
        );

        profileCommand.getMainCommand().getProfileClient().addProfile(profile, basePath);

        System.out.println(
                CommandLine.Help.Ansi.AUTO.text("@|bold,underline,bg(60),fg(46) Start (restart) ddns daemon for apply created profile.|@")
        );

        return OK;
    }

    private String getProfileName() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter profile name:");
        String name = scanner.nextLine();
        if (name.isEmpty()) {
            throw new PicocliException("Invalid profile name, try again.");
        }
        return name;
    }

    private void init() {
        basePath = profileCommand.getMainCommand().getBasePath();
    }
}
