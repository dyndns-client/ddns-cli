package ddns.cli.command;

import ddns.cli.command.client.ClientCommand;
import ddns.cli.command.config.ConfigCommand;
import ddns.cli.command.profile.ProfileCommand;
import ddns.client.daemon.DaemonClient;
import ddns.client.profile.ProfileClient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import static ddns.client.constant.Defaults.DDNS_CLI_BASE_PATH;

@Command(name = "ddns-cli",
        version = "v1.0.0",
        mixinStandardHelpOptions = true,
        subcommands = {ClientCommand.class, ProfileCommand.class, ConfigCommand.class})
@RequiredArgsConstructor
@Getter
public class MainCommand {

    private final DaemonClient daemonClient;
    private final ProfileClient profileClient;

    @Option(names = {"--base-dir"},
            description = "Path to ddns-cli base directory, by default: ~/.ddns-cli")
    private String basePath = DDNS_CLI_BASE_PATH;
}
