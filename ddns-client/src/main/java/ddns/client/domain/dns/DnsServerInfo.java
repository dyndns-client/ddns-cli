package ddns.client.domain.dns;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DNS public IP lookup system
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DnsServerInfo {
    private String domainName;
    private String dnsResolverName;
}
