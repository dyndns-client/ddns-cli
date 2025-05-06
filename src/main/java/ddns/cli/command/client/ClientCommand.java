package ddns.cli.command.client;

import ddns.cli.command.MainCommand;
import ddns.cli.command.client.start.ClientStartCommand;
import lombok.Getter;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(name = "client",
        description = "Dynamic DNS Client",
        mixinStandardHelpOptions = true,
        subcommands = {ClientStartCommand.class})
@Getter
public class ClientCommand {

    @ParentCommand
    private MainCommand mainCommand;
}
