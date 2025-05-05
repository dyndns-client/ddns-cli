package ddns.client.daemon;

import com.google.gson.Gson;
import ddns.client.discovery.DnsDiscoveryService;
import ddns.client.discovery.HttpDiscoveryService;
import ddns.client.discovery.StunDiscoveryService;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DaemonClientBaseImpl implements DaemonClient {

    private final StunDiscoveryService stunDiscoveryService;
    private final DnsDiscoveryService dnsDiscoveryService;
    private final HttpDiscoveryService httpDiscoveryService;
    private final Gson gson;

    @Override
    public void start(String basePath) {

    }

    @Override
    public void stop() {

    }
}
