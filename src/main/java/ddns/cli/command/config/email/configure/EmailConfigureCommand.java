package ddns.cli.command.config.email.configure;

import ddns.cli.command.config.email.EmailCommand;
import ddns.cli.command.config.email.handler.EmailHandler;
import ddns.client.email.EmailInfo;
import ddns.client.email.EmailServiceBaseImpl;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.util.concurrent.Callable;

import static picocli.CommandLine.ExitCode.OK;

@Command(name = "configure",
        description = "Configuring email info for receive IP update mails",
        mixinStandardHelpOptions = true)
public class EmailConfigureCommand implements Callable<Integer> {

    private final EmailHandler emailHandler = new EmailHandler();
    private final EmailServiceBaseImpl emailService = new EmailServiceBaseImpl();

    @ParentCommand
    private EmailCommand parent;

    @Override
    public Integer call() {
        EmailInfo emailInfo = emailHandler.handle();
        emailService.saveEmailInfo(parent.getParent().getMainCommand().getBasePath(), emailInfo);
        return OK;
    }
}
