package ddns.cli.command.client.start;

import ddns.cli.command.client.ClientCommand;
import lombok.Getter;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.util.concurrent.Callable;

import static picocli.CommandLine.ExitCode.OK;

@Command(name = "start",
        description = "Start Dynamic DNS Client",
        mixinStandardHelpOptions = true)
@Getter
public class ClientStartCommand implements Callable<Integer> {

    @ParentCommand
    private ClientCommand clientCommand;

    @Override
    public Integer call() {
        clientCommand.getMainCommand().getDaemonClient().start(clientCommand.getMainCommand().getBasePath());
        return OK;
    }
}
