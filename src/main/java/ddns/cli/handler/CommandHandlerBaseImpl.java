package ddns.cli.handler;

import ddns.cli.command.MainCommand;
import ddns.client.daemon.DaemonClient;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommandHandlerBaseImpl implements CommandHandler {

    private final DaemonClient daemonClient;

    @Override
    public void handle(MainCommand command) {
        System.out.println(daemonClient);
    }
}
