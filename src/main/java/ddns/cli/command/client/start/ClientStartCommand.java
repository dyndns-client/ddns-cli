package ddns.cli.command.client.start;

import ddns.cli.command.client.ClientCommand;
import lombok.Getter;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.util.concurrent.Callable;

import static picocli.CommandLine.ExitCode.OK;

@Command(name = "start",
        description = "Start Dynamic DNS Client")
@Getter
public class ClientStartCommand implements Callable<Integer> {

    @ParentCommand
    private ClientCommand clientCommand;

    @Override
    public Integer call() throws Exception {
        System.out.println("Start command executed");
        clientCommand.getMainCommand().getDaemonClient().start(clientCommand.getMainCommand().getBasePath());
        return OK;
    }
}
