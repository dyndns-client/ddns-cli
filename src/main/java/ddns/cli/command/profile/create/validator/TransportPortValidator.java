package ddns.cli.command.profile.create.validator;

import picocli.CommandLine.PicocliException;

public class TransportPortValidator {

    public static void validate(int port) {
        if (port < 0 || port > 65535) {
            throw new PicocliException("Invalid port number, try again.");
        }
    }
}
