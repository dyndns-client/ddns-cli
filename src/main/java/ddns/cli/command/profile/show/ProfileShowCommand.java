package ddns.cli.command.profile.show;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ddns.cli.command.profile.ProfileCommand;
import ddns.client.domain.Profile;
import ddns.client.profile.ProfileClient;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import java.util.Optional;
import java.util.concurrent.Callable;

import static picocli.CommandLine.ExitCode.OK;

@Command(name = "show",
        description = "Show profile",
        mixinStandardHelpOptions = true)
public class ProfileShowCommand implements Callable<Integer> {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Option(names = "--name", description = "Profile name", required = true)
    private String name;

    @ParentCommand
    private ProfileCommand profileCommand;

    @Override
    public Integer call() {
        ProfileClient profileClient = profileCommand.getMainCommand().getProfileClient();
        Optional<Profile> profile = profileClient.getProfile(name, profileCommand.getMainCommand().getBasePath());
        if (profile.isPresent()) {
            String json = gson.toJson(profile.get(), Profile.class);
            System.out.println(json);
        } else {
            System.out.println("Profile not found.");
        }
        return OK;
    }
}
