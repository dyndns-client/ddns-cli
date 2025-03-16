package ddns.cli;

import ddns.cli.command.MainCommand;
import picocli.CommandLine;

public class App {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new MainCommand()).execute(args);
    }
}
