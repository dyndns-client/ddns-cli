package ddns.client.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

import static ddns.client.constant.Defaults.DEFAULT_DISCOVERY_INTERVAL;
import static ddns.client.constant.Defaults.DEFAULT_DISCOVERY_SOCKET_TIMEOUT;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public abstract class DiscoveryInfo<T> {
    /**
     * Discovery servers. If more than one server is passed, then requests will be executed in Round-robin.
     */
    private List<T> servers;

    /**
     * IP discovery interval in seconds. Must be divisible by 10
     */
    @Builder.Default
    private int discoveryInterval = DEFAULT_DISCOVERY_INTERVAL;

    /**
     * Discovery socket timeout
     */
    @Builder.Default
    private int socketTimeout = DEFAULT_DISCOVERY_SOCKET_TIMEOUT;
}
