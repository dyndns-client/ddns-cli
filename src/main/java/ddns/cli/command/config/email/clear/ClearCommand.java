package ddns.cli.command.config.email.clear;

import ddns.cli.command.config.email.EmailCommand;
import ddns.client.email.EmailService;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.util.concurrent.Callable;

import static picocli.CommandLine.ExitCode.OK;

@Command(name = "clear",
        description = "Clear email info for receive IP update mails",
        mixinStandardHelpOptions = true)
public class ClearCommand implements Callable<Integer> {

    @ParentCommand
    private EmailCommand parent;

    @Override
    public Integer call() {
        EmailService emailService = parent.getParent().getMainCommand().getEmailService();
        boolean cleared = emailService.clear(parent.getParent().getMainCommand().getBasePath());
        if (cleared) {
            System.out.println(
                    CommandLine.Help.Ansi.AUTO.text("@|bold,underline,bg(60),fg(46) Email info was cleared.|@")
            );
        } else {
            System.out.println(
                    CommandLine.Help.Ansi.AUTO.text("No Email info saved.")
            );
        }
        return OK;
    }
}
