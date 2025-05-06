package ddns.cli.command;

import ddns.cli.command.client.ClientCommand;
import ddns.cli.command.profile.ProfileCommand;
import ddns.client.daemon.DaemonClient;
import ddns.client.profile.ProfileClient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "ddns-cli",
        version = "v1.0.0",
        mixinStandardHelpOptions = true,
        subcommands = {ClientCommand.class, ProfileCommand.class})
@RequiredArgsConstructor
@Getter
public class MainCommand {

    private final DaemonClient daemonClient;
    private final ProfileClient profileClient;

    @Option(names = {"--base-dir"},
            description = "Path to ddns-cli base directory",
            defaultValue = "~./ddns-cli",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    private String basePath;

}
