package ddns.client.domain;

import ddns.client.domain.dns.DnsDiscoveryInfo;
import ddns.client.domain.http.HttpDiscoveryInfo;
import ddns.client.domain.stun.StunDiscoveryInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Profile {
    /**
     * Profile name. Unique field
     */
    private String name;

    /**
     * Is profile active
     */
    private boolean active;

    /**
     * IP version. IPv4 by default
     */
    @Builder.Default
    private IPVersion ipVersion = IPVersion.IPv4;

    /**
     * Dynamic DNS Provider info for update IP
     */
    private AddressUpdateInfo updateInfo;

    /**
     * Last updated IP
     */
    private IP lastUpdateIp;

    /**
     * Discovery method
     */
    private DiscoveryMethod discoveryMethod;

    /**
     * STUN discovery info
     */
    private StunDiscoveryInfo stunDiscoveryInfo;

    /**
     * HTTP discovery info
     */
    private HttpDiscoveryInfo httpDiscoveryInfo;

    /**
     * DNS discovery info
     */
    private DnsDiscoveryInfo dnsDiscoveryInfo;
}
