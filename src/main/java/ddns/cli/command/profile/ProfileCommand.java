package ddns.cli.command.profile;

import ddns.cli.command.MainCommand;
import ddns.cli.command.profile.create.ProfileCreateCommand;
import lombok.Getter;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(name = "profile",
        description = "Configuring profiles",
        mixinStandardHelpOptions = true,
        subcommands = {ProfileCreateCommand.class})
@Getter
public class ProfileCommand {

    @ParentCommand
    private MainCommand mainCommand;
}
