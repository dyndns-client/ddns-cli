package ddns.cli.command.config.auth.configure;

import ddns.cli.command.config.auth.AuthCommand;
import ddns.cli.command.config.auth.handler.AuthHandler;
import ddns.client.auth.AuthService;
import ddns.client.auth.AuthServiceImpl;
import ddns.client.domain.Auth;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.util.concurrent.Callable;

import static picocli.CommandLine.ExitCode.OK;

@Command(name = "configure",
        description = "Configuring auth info.",
        mixinStandardHelpOptions = true)
public class AuthConfigureCommand implements Callable<Integer> {

    @ParentCommand
    private AuthCommand parent;

    @Override
    public Integer call() throws Exception {
        AuthService authService = new AuthServiceImpl();
        AuthHandler authHandler = new AuthHandler();

        Auth auth = authHandler.handle();
        authService.saveAuth(auth, parent.getParent().getMainCommand().getBasePath());
        System.out.println(
                CommandLine.Help.Ansi.AUTO.text("@|bold,underline,bg(60),fg(46) Auth info saved!|@")
        );
        return OK;
    }
}
