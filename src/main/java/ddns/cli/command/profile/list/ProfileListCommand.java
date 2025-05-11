package ddns.cli.command.profile.list;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ddns.cli.command.profile.ProfileCommand;
import ddns.client.domain.Profile;
import ddns.client.profile.ProfileClient;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static picocli.CommandLine.ExitCode.OK;

@Command(name = "list",
        description = "List saved profiles",
        mixinStandardHelpOptions = true)
public class ProfileListCommand implements Callable<Integer> {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @ParentCommand
    private ProfileCommand profileCommand;

    @Override
    public Integer call() {
        ProfileClient profileClient = profileCommand.getMainCommand().getProfileClient();
        List<String> profiles = profileClient.getProfiles(profileCommand.getMainCommand().getBasePath()).stream()
                .map(Profile::getName)
                .collect(Collectors.toList());
        gson.toJson(profiles, System.out);
        return OK;
    }
}
