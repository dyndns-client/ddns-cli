package ddns.cli.command;

import ddns.client.daemon.DaemonClient;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

import static picocli.CommandLine.ExitCode.OK;

@Command(name = "main command", version = "v1.0.0", mixinStandardHelpOptions = true)
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class MainCommand implements Callable<Integer> {

    private final DaemonClient daemonClient;

    @Override
    public Integer call() {
        System.out.println("Main command executed");
        daemonClient.start();
        return OK;
    }
}
