package ddns.client.daemon;

import com.google.gson.Gson;
import ddns.client.discovery.DnsDiscoveryService;
import ddns.client.discovery.HttpDiscoveryService;
import ddns.client.discovery.StunDiscoveryService;
import ddns.client.domain.DiscoveryMethod;
import ddns.client.domain.IP;
import ddns.client.domain.IPVersion;
import ddns.client.domain.Profile;
import ddns.client.domain.http.HttpServerInfo;
import ddns.client.domain.stun.StunDiscoveryInfo;
import ddns.client.domain.stun.StunServerInfo;
import ddns.client.exception.DiscoveryException;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DaemonClientBaseImpl implements DaemonClient {

    private final StunDiscoveryService stunDiscoveryService;
    private final DnsDiscoveryService dnsDiscoveryService;
    private final HttpDiscoveryService httpDiscoveryService;
    private final Gson gson;

    @Override
    public void start(String basePath) {
        Profile profile = Profile.builder()
                .name("DuckDNS profile 1")
                .ipVersion(IPVersion.IPV4)
                .discoveryInterval(30)
                .discoveryMethod(DiscoveryMethod.STUN)
                .stunDiscoveryInfo(StunDiscoveryInfo.builder()
                        .servers(List.of(StunServerInfo.builder().host("ad").port(80).build())).build()
                )
                .build();
        String json = gson.toJson(profile);
        System.out.println(json);
        Profile fromJson = gson.fromJson(json, Profile.class);
        System.out.println(fromJson);

        try {
            IP publicIP = httpDiscoveryService.discover(HttpServerInfo.builder().build(), 5);
        } catch (DiscoveryException e) {
            throw new RuntimeException(e);
        }

        System.out.println("starting ddns client...");
    }

    @Override
    public void startDetached(String basePath) {
        System.out.println("starting ddns client...");
    }

    @Override
    public void stop() {
        System.out.println("stopping ddns client...");
    }
}
