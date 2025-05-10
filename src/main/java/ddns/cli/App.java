package ddns.cli;

import ddns.cli.command.MainCommand;
import ddns.client.config.di.component.DaggerClientComponent;
import ddns.client.daemon.DaemonClient;
import ddns.client.profile.ProfileClient;
import picocli.CommandLine;

public class App {

    public static void main(String[] args) {
        DaemonClient daemonClient = DaggerClientComponent.create().buildDaemonClient();
        ProfileClient profileClient = DaggerClientComponent.create().buildProfileClient();

        CommandLine cmd = new CommandLine(new MainCommand(daemonClient, profileClient));
        new CommandLineConfigurer().configure(cmd);
        int exitCode = cmd.execute(args);
        System.exit(exitCode);
    }
}
