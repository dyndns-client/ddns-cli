package ddns.cli.command.config.email;

import ddns.cli.command.config.ConfigCommand;
import ddns.cli.command.config.email.configure.EmailConfigureCommand;
import ddns.cli.command.config.email.info.EmailInfoCommand;
import lombok.Getter;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(name = "email",
        description = "Configuring and show email info for receive IP update mails",
        mixinStandardHelpOptions = true,
        subcommands = {EmailConfigureCommand.class, EmailInfoCommand.class})
@Getter
public class EmailCommand {

    @ParentCommand
    private ConfigCommand parent;
}
