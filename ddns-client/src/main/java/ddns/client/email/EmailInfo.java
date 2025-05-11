package ddns.client.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Properties;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EmailInfo {
    private Properties smtpProps;
    private String from;
    private String to;
    private String password;
    private String subject;
}
