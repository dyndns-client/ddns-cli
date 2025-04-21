package ddns.cli.command;

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
    private MainCommand mainCommand;

    @Override
    public Integer call() throws Exception {
        System.out.println("Start command executed");
        mainCommand.getDaemonClient().start(mainCommand.getBasePath());
        return OK;
    }
}
