package ddns.cli.command.config.auth.clear;

import ddns.cli.command.config.auth.AuthCommand;
import ddns.client.auth.AuthService;
import ddns.client.auth.AuthServiceImpl;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.util.concurrent.Callable;

import static picocli.CommandLine.ExitCode.OK;

@Command(name = "clear",
        description = "Clear auth info.",
        mixinStandardHelpOptions = true)
public class AuthClearCommand implements Callable<Integer> {

    @ParentCommand
    private AuthCommand parent;

    @Override
    public Integer call() {
        AuthService authService = new AuthServiceImpl();
        boolean cleared = authService.clear(parent.getParent().getMainCommand().getBasePath());
        if (cleared) {
            System.out.println(
                    CommandLine.Help.Ansi.AUTO.text("@|bold,underline,bg(60),fg(46) Auth info was cleared.|@")
            );
        } else {
            System.out.println(
                    CommandLine.Help.Ansi.AUTO.text("No saved auth info.")
            );
        }
        return OK;
    }
}
