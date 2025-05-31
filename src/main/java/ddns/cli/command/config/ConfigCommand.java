package ddns.cli.command.config;

import ddns.cli.command.MainCommand;
import ddns.cli.command.config.auth.AuthCommand;
import ddns.cli.command.config.email.EmailCommand;
import lombok.Getter;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(name = "config",
        description = "Dynamic DNS Configuration.",
        mixinStandardHelpOptions = true,
        subcommands = {EmailCommand.class, AuthCommand.class})
@Getter
public class ConfigCommand {

    @ParentCommand
    private MainCommand mainCommand;
}
