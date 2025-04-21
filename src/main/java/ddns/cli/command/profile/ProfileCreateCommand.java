package ddns.cli.command.profile;

import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(name = "create",
        description = "Creating profile")
public class ProfileCreateCommand {

    @ParentCommand
    private ProfileCommand profileCommand;
}
