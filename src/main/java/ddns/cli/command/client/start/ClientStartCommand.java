package ddns.cli.command.client.start;

import ddns.cli.command.client.ClientCommand;
import ddns.client.auth.AuthService;
import ddns.client.auth.AuthServiceImpl;
import ddns.client.daemon.DaemonClient;
import ddns.client.domain.Auth;
import ddns.webapi.WebApi;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.hc.core5.http.impl.bootstrap.HttpServer;
import org.apache.hc.core5.util.TimeValue;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;
import picocli.CommandLine.PicocliException;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static picocli.CommandLine.ExitCode.OK;

@Command(name = "start",
        description = "Start Dynamic DNS Client.",
        mixinStandardHelpOptions = true)
@Getter
public class ClientStartCommand implements Callable<Integer> {

    @ParentCommand
    private ClientCommand clientCommand;

    @Option(names = "--web-api-port")
    private int port;

    @SneakyThrows
    @Override
    public Integer call() {
        if (port < 0 || port > 65535) {
            throw new PicocliException("Invalid port.");
        }
        checkAuth(clientCommand.getMainCommand().getBasePath());
        DaemonClient daemonClient = clientCommand.getMainCommand().getDaemonClient();
        String basePath = clientCommand.getMainCommand().getBasePath();
        daemonClient.start(basePath);
        long pid = ProcessHandle.current().pid();
        if (port != 0) {
            WebApi webApi = new WebApi();
            try (HttpServer server = webApi.start(port, basePath, clientCommand.getMainCommand().getProfileClient(), daemonClient)) {
                server.start();
                System.out.println(
                        CommandLine.Help.Ansi.AUTO.text("@|bold,underline,bg(60),fg(46) Dynamic DNS Client started with PID: " + pid + " and port: " + port +"|@")
                );
                server.awaitTermination(TimeValue.MAX_VALUE);
            }
        } else {
            System.out.println(
                    CommandLine.Help.Ansi.AUTO.text("@|bold,underline,bg(60),fg(46) Dynamic DNS Client started with PID: " + pid + "|@")
            );
        }
        boolean ignore = Executors.newSingleThreadExecutor().awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        return OK;
    }

    private void checkAuth(String basePath) {
        if (port != 0) {
            AuthService authService = new AuthServiceImpl();
            Optional<Auth> auth = authService.getAuth(basePath);
            if (auth.isEmpty()) {
                throw new PicocliException("Configure auth for web api with command: " + "config auth configure");
            }
        }
    }
}
