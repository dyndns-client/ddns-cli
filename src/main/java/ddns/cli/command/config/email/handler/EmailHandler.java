package ddns.cli.command.config.email.handler;

import ddns.client.domain.email.EmailInfo;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import picocli.CommandLine.PicocliException;

import java.util.Properties;
import java.util.Scanner;

public class EmailHandler {

    private final Scanner scanner = new Scanner(System.in);

    public EmailInfo handle() {
        System.out.println("Enter email address from:");
        String from = scanner.nextLine();
        try {
            new InternetAddress(from);
        } catch(Exception e) {
            throw new PicocliException("Invalid email address, try again.");
        }

        System.out.println("Enter email address to:");
        String to = scanner.nextLine();
        try {
            InternetAddress.parse(to);
        } catch (AddressException e) {
            throw new PicocliException("Invalid email address, try again.");
        }

        Properties smtpProps = new Properties();
        smtpProps.put("mail.smtp.auth", "true");

        System.out.println("Enter 'y' if need use mail.smtp.ssl, or 'n' otherwise");
        String useSmtpSsl = scanner.nextLine();
        if (!"y".equals(useSmtpSsl) && !"n".equals(useSmtpSsl)) {
            throw new PicocliException("Invalid option value, try again.");
        }
        boolean ssl = useSmtpSsl.equals("y");
        smtpProps.put("mail.smtp.ssl.enable", String.valueOf(ssl));

        if (!ssl) {
            System.out.println("Enter 'y' if need use mail.smtp.starttls, or 'n' otherwise");
            String useStarttls = scanner.nextLine();
            if (!"y".equals(useStarttls) && !"n".equals(useStarttls)) {
                throw new PicocliException("Invalid option value, try again.");
            }
            boolean starttls = useStarttls.equals("y");
            smtpProps.put("mail.smtp.starttls.enable", String.valueOf(starttls));
        }

        System.out.println("Enter smtp host:");
        String smtpHost = scanner.nextLine();
        if (smtpHost.isEmpty()) {
            throw new PicocliException("Invalid smtp host, try again.");
        }
        smtpProps.put("mail.smtp.host", smtpHost);

        System.out.println("Enter smtp port:");
        String smtpPort = scanner.nextLine();
        if (smtpPort.isEmpty()) {
            throw new PicocliException("Invalid smtp port, try again.");
        }
        smtpProps.put("mail.smtp.port", smtpPort);

        System.out.println("Enter smtp (application) password:");
        String smtpPassword = scanner.nextLine();
        if (smtpPassword.isEmpty()) {
            throw new PicocliException("Invalid smtp password, try again.");
        }

        System.out.println("Enter mail subject:");
        String subject = scanner.nextLine();
        if (subject.isEmpty()) {
            throw new PicocliException("Invalid mail subject, try again.");
        }

        return EmailInfo.builder()
                .from(from)
                .to(to)
                .password(smtpPassword)
                .smtpProps(smtpProps)
                .subject(subject)
                .build();
    }
}
