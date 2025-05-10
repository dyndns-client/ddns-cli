package ddns.client.email;

import java.util.Optional;

public interface EmailService {

    void saveEmailInfo(String basePath, EmailInfo emailInfo);

    Optional<EmailInfo> getEmailInfo(String basePath);
}
