package ddns.cli.command;

import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

import static picocli.CommandLine.ExitCode.OK;

@Command(name = "main command", version = "v1.0.0", mixinStandardHelpOptions = true)
public class MainCommand implements Callable<Integer> {

    @Override
    public Integer call() {
        System.out.println("Main command executed");
        return OK;
    }
}
