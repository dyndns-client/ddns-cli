package ddns.client.email;

import ddns.client.domain.IP;
import ddns.client.domain.email.EmailInfo;

import java.util.Optional;

public interface EmailService {

    void sendEmail(EmailInfo emailInfo, IP currentIp);

    void saveEmailInfo(String basePath, EmailInfo emailInfo);

    Optional<EmailInfo> getEmailInfo(String basePath);

    boolean clear(String basePath);
}
