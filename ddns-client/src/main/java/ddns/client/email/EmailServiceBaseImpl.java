package ddns.client.email;

import com.google.gson.Gson;
import ddns.client.domain.IP;
import jakarta.inject.Inject;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class EmailServiceBaseImpl implements EmailService {

    private final Gson gson;

    @Override
    public void sendEmail(EmailInfo emailInfo, IP currentIp) {
        String to = emailInfo.getTo();
        String from = emailInfo.getFrom();

        Properties properties = emailInfo.getSmtpProps();

        String username = from;
        String password = emailInfo.getPassword();

        Session session = Session.getInstance(properties,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to)
            );
            message.setSubject(emailInfo.getSubject());
            message.setText("IP updated to " + currentIp.getValue());

            Transport.send(message);

            System.out.println("Email sent to " + to);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    public void saveEmailInfo(String basePath, EmailInfo emailInfo) {
        basePath += "/config";
        File file = new File(basePath);
        if (!file.exists()) {
            boolean ignore = file.mkdirs();
        }

        basePath += "/email-config.json";
        File config = new File(basePath);
        if (!config.exists()) {
            boolean ignore = config.createNewFile();
        }

        String json = gson.toJson(emailInfo);
        try (FileWriter writer = new FileWriter(config, false)) {
            writer.write(json);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<EmailInfo> getEmailInfo(String basePath) {
        basePath += "/config";
        File file = new File(basePath);
        if (!file.exists()) {
            return Optional.empty();
        }

        basePath += "/email-config.json";
        File config = new File(basePath);
        if (!config.exists()) {
            return Optional.empty();
        }

        try (FileReader reader = new FileReader(config)) {
            EmailInfo emailInfo = gson.fromJson(reader, EmailInfo.class);
            return Optional.of(emailInfo);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean clear(String basePath) {
        basePath += "/config";
        File file = new File(basePath);
        if (!file.exists()) {
            return false;
        }

        basePath += "/email-config.json";
        File config = new File(basePath);
        if (!config.exists()) {
            return false;
        }

        return config.delete();
    }
}
