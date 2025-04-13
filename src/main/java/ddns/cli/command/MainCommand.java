package ddns.cli.command;

import ddns.client.daemon.DaemonClient;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

import static picocli.CommandLine.ExitCode.OK;

@Command(name = "main command", version = "v1.0.0", mixinStandardHelpOptions = true)
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class MainCommand implements Callable<Integer> {

    private final DaemonClient daemonClient;

    @Option(names = {"--base-dir"},
            description = "Path to ddns-cli base directory",
            defaultValue = "~./ddns-cli",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    private String basePath;

    @Override
    public Integer call() {
        System.out.println("Main command executed");
        System.out.println(basePath);
        daemonClient.start();
        return OK;
    }
}
