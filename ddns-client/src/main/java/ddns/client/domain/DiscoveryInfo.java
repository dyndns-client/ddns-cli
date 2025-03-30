package ddns.client.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public abstract class DiscoveryInfo<T> {
    private List<T> servers;
    private boolean circular;
    private int socketTimeout;
}
