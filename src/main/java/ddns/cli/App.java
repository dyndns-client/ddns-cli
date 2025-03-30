package ddns.cli;

import ddns.cli.command.MainCommand;
import ddns.client.config.component.DaggerClientComponent;
import ddns.client.daemon.DaemonClient;
import picocli.CommandLine;

public class App {

    public static void main(String[] args) {
        DaemonClient daemonClient = DaggerClientComponent.create().buildDaemonClient();
        int exitCode = new CommandLine(new MainCommand(daemonClient)).execute(args);
    }
}
