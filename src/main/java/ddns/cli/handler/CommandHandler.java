package ddns.cli.handler;

import ddns.cli.command.MainCommand;

public interface CommandHandler {

    void handle(MainCommand command);
}
