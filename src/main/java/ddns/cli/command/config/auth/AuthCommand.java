package ddns.cli.command.config.auth;

import ddns.cli.command.config.ConfigCommand;
import ddns.cli.command.config.auth.clear.AuthClearCommand;
import ddns.cli.command.config.auth.configure.AuthConfigureCommand;
import ddns.cli.command.config.auth.show.AuthShowCommand;
import lombok.Getter;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(name = "auth",
        description = "Configuring and show auth info for Web login.",
        mixinStandardHelpOptions = true,
        subcommands = {AuthClearCommand.class, AuthConfigureCommand.class, AuthShowCommand.class})
@Getter
public class AuthCommand {

    @ParentCommand
    private ConfigCommand parent;
}
