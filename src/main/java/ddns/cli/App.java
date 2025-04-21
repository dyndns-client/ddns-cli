package ddns.cli;

import ddns.cli.command.MainCommand;
import ddns.client.config.component.DaggerClientComponent;
import ddns.client.daemon.DaemonClient;
import ddns.client.profile.ProfileClient;
import picocli.CommandLine;

public class App {

    public static void main(String[] args) {
        DaemonClient daemonClient = DaggerClientComponent.create().buildDaemonClient();
        ProfileClient profileClient = DaggerClientComponent.create().buildProfileClient();

        int exitCode = new CommandLine(new MainCommand(daemonClient, profileClient)).execute(args);
        System.exit(exitCode);
    }
}
