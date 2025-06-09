package ddns.client.domain.http;

import ddns.client.domain.DiscoveryInfo;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class HttpDiscoveryInfo extends DiscoveryInfo<HttpServerInfo> {
}
