package ddns.cli.command.config.auth.show;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ddns.cli.command.config.auth.AuthCommand;
import ddns.client.auth.AuthService;
import ddns.client.auth.AuthServiceImpl;
import ddns.client.domain.Auth;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.util.Optional;
import java.util.concurrent.Callable;

import static picocli.CommandLine.ExitCode.OK;

@Command(name = "show",
        description = "Show Auth info.",
        mixinStandardHelpOptions = true)
public class AuthShowCommand implements Callable<Integer> {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @ParentCommand
    private AuthCommand parent;

    @Override
    public Integer call() throws Exception {
        AuthService authService = new AuthServiceImpl();
        Optional<Auth> auth = authService.getAuth(parent.getParent().getMainCommand().getBasePath());
        if (auth.isPresent()) {
            System.out.println(gson.toJson(auth.get()));
        } else {
            System.out.println("No saved auth info.");
        }
        return OK;
    }
}
