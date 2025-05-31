package ddns.cli.command.config.email.show;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ddns.cli.command.config.email.EmailCommand;
import ddns.client.domain.email.EmailInfo;
import ddns.client.email.EmailService;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.util.Optional;
import java.util.concurrent.Callable;

import static picocli.CommandLine.ExitCode.OK;

@Command(name = "show",
        description = "Show email info for receive IP update mails.",
        mixinStandardHelpOptions = true)
public class EmailShowCommand implements Callable<Integer> {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @ParentCommand
    private EmailCommand parent;

    @Override
    public Integer call() {
        EmailService emailService = parent.getParent().getMainCommand().getEmailService();
        Optional<EmailInfo> emailInfo = emailService.getEmailInfo(parent.getParent().getMainCommand().getBasePath());
        if (emailInfo.isPresent()) {
            System.out.println(gson.toJson(emailInfo.get()));
        } else {
            System.out.println("No saved email info.");
        }
        return OK;
    }
}
