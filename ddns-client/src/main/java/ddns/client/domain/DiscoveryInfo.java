package ddns.client.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import static ddns.client.constant.Defaults.DEFAULT_DISCOVERY_INTERVAL;
import static ddns.client.constant.Defaults.DEFAULT_DISCOVERY_SOCKET_TIMEOUT;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public abstract class DiscoveryInfo<T> {
    /**
     * Discovery server
     */
    private T server;

    /**
     * IP discovery interval in seconds
     */
    @Builder.Default
    private int discoveryInterval = DEFAULT_DISCOVERY_INTERVAL;

    /**
     * Discovery socket timeout
     */
    @Builder.Default
    private int socketTimeout = DEFAULT_DISCOVERY_SOCKET_TIMEOUT;
}
