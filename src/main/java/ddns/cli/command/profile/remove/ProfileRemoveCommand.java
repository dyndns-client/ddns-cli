package ddns.cli.command.profile.remove;

import ddns.cli.command.profile.ProfileCommand;
import ddns.client.domain.Profile;
import ddns.client.profile.ProfileClient;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;
import picocli.CommandLine.PicocliException;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

import static picocli.CommandLine.ExitCode.OK;

@Command(name = "remove",
        description = "Remove profile.",
        mixinStandardHelpOptions = true)
public class ProfileRemoveCommand implements Callable<Integer> {

    @Option(names = "--name")
    private String name;

    @Option(names = "--all")
    private boolean all;

    @ParentCommand
    private ProfileCommand profileCommand;

    @Override
    public Integer call() {
        ProfileClient profileClient = profileCommand.getMainCommand().getProfileClient();

        if (Objects.nonNull(name)) {
            boolean remove = profileClient.removeProfile(name, profileCommand.getMainCommand().getBasePath());
            if (remove) {
                System.out.println(
                        CommandLine.Help.Ansi.AUTO.text("@|bold,underline,bg(60),fg(46) Profile was removed.|@")
                );
            } else {
                System.out.println(
                        CommandLine.Help.Ansi.AUTO.text("No profile saved.")
                );
            }
        } else if (all) {
            List<Profile> profiles = profileClient.getProfiles(profileCommand.getMainCommand().getBasePath());
            if (profiles.isEmpty()) {
                System.out.println(
                        CommandLine.Help.Ansi.AUTO.text("No profile saved.")
                );
            }
            for (Profile profile : profiles) {
                profileClient.removeProfile(profile.getName(), profileCommand.getMainCommand().getBasePath());
            }
            System.out.println(
                    CommandLine.Help.Ansi.AUTO.text("@|bold,underline,bg(60),fg(46) All profile was removed.|@")
            );
        } else {
            throw new PicocliException("No option passed.");
        }
        return OK;
    }
}
