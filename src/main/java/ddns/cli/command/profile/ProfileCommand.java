package ddns.cli.command.profile;

import ddns.cli.command.MainCommand;
import ddns.cli.command.profile.create.ProfileCreateCommand;
import ddns.cli.command.profile.list.ProfileListCommand;
import ddns.cli.command.profile.remove.ProfileRemoveCommand;
import ddns.cli.command.profile.show.ProfileShowCommand;
import lombok.Getter;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(name = "profile",
        description = "Profiles Configuration.",
        mixinStandardHelpOptions = true,
        subcommands = {ProfileCreateCommand.class, ProfileListCommand.class, ProfileShowCommand.class, ProfileRemoveCommand.class})
@Getter
public class ProfileCommand {

    @ParentCommand
    private MainCommand mainCommand;
}
