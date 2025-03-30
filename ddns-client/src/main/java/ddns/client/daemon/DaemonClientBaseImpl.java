package ddns.client.daemon;

import com.google.gson.Gson;
import ddns.client.discovery.StunDiscoveryService;
import ddns.client.domain.DiscoveryMethod;
import ddns.client.domain.IPVersion;
import ddns.client.domain.Profile;
import ddns.client.domain.stun.StunDiscoveryInfo;
import ddns.client.domain.stun.StunServerInfo;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DaemonClientBaseImpl implements DaemonClient {

    private final StunDiscoveryService stunDiscoveryService;
    private final Gson gson;

    @Override
    public void start() {
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

        System.out.println("starting ddns client...");
    }

    @Override
    public void startDetached() {
        System.out.println("starting ddns client...");
    }

    @Override
    public void stop() {
        System.out.println("stopping ddns client...");
    }
}
