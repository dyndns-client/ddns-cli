package ddns.cli.command.config.email.configure;

import ddns.cli.command.config.email.EmailCommand;
import ddns.cli.command.config.email.handler.EmailHandler;
import ddns.client.domain.email.EmailInfo;
import ddns.client.email.EmailService;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.util.concurrent.Callable;

import static picocli.CommandLine.ExitCode.OK;

@Command(name = "configure",
        description = "Configuring email info for receive IP update mails.",
        mixinStandardHelpOptions = true)
public class EmailConfigureCommand implements Callable<Integer> {

    private final EmailHandler emailHandler = new EmailHandler();

    @ParentCommand
    private EmailCommand parent;

    @Override
    public Integer call() {
        EmailInfo emailInfo = emailHandler.handle();
        EmailService emailService = parent.getParent().getMainCommand().getEmailService();
        emailService.saveEmailInfo(parent.getParent().getMainCommand().getBasePath(), emailInfo);
        System.out.println(
                CommandLine.Help.Ansi.AUTO.text("@|bold,underline,bg(60),fg(46) Email info saved!|@")
        );
        return OK;
    }
}
