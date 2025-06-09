package ddns.cli.command.config.auth.handler;

import ddns.client.domain.Auth;
import picocli.CommandLine.PicocliException;

import java.util.Scanner;

public class AuthHandler {

    private final Scanner scanner = new Scanner(System.in);

    public Auth handle() {
        System.out.println("Enter username:");
        String username = scanner.nextLine();
        if (username.isEmpty()) {
            throw new PicocliException("Username cannot be empty.");
        }

        System.out.println("Enter password:");
        String password = scanner.nextLine();
        if (password.isEmpty()) {
            throw new PicocliException("Username cannot be empty.");
        }

        int seconds = 604800;
        System.out.println("Enter session duration is seconds, or press 'ENTER' for use default: " + seconds);
        String sessionDuration = scanner.nextLine();
        if (!sessionDuration.isEmpty()) {
            try {
                seconds = Integer.parseInt(sessionDuration);
                if (seconds < 0) {
                    throw new PicocliException("Session duration cannot be negative.");
                }
            } catch (Exception e) {
                if (e instanceof PicocliException) {
                    throw e;
                }
                throw new PicocliException("Invalid session duration: " + sessionDuration);
            }
        }

        return new Auth(username, password, seconds);
    }
}
