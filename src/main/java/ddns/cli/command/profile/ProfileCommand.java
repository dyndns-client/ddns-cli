package ddns.cli.command.profile;

import ddns.cli.command.MainCommand;
import lombok.Getter;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(name = "profile",
        description = "Configuring profiles")
@Getter
public class ProfileCommand {

    @ParentCommand
    private MainCommand mainCommand;
}
