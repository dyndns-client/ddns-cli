package ddns.client.daemon;

import com.google.gson.Gson;
import ddns.client.discovery.DnsDiscoveryService;
import ddns.client.discovery.HttpDiscoveryService;
import ddns.client.discovery.StunDiscoveryService;
import ddns.client.domain.DiscoveryMethod;
import ddns.client.domain.IP;
import ddns.client.domain.Profile;
import ddns.client.profile.ProfileClient;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DaemonClientBaseImpl implements DaemonClient {

    private final ProfileClient profileClient;
    private final StunDiscoveryService stunDiscoveryService;
    private final DnsDiscoveryService dnsDiscoveryService;
    private final HttpDiscoveryService httpDiscoveryService;
    private final Gson gson;

    @SneakyThrows
    @Override
    public void start(String basePath) {
        List<Profile> profiles = profileClient.getProfiles(basePath);

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(profiles.size());
        for (Profile profile : profiles) {
            int delay = getDelay(profile);
            executor.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    // делаем discovery, смотрим переменную lastIpUpdate, если там такой же IP ничего не делаем
                    // если там нет ничего - делаем InetAddress.getByName()
                    // если в одном из 2-х случаев IP отличается - обновляем и отправляем письмо на почту
                }
            }, 0, delay, TimeUnit.SECONDS);
        }
    }

    @Override
    public void stop() {

    }

    private int getDelay(Profile profile) {
        DiscoveryMethod discoveryMethod = profile.getDiscoveryMethod();
        return switch (discoveryMethod) {
            case STUN -> profile.getStunDiscoveryInfo().getDiscoveryInterval();
            case DNS -> profile.getDnsDiscoveryInfo().getDiscoveryInterval();
            case HTTP -> profile.getHttpDiscoveryInfo().getDiscoveryInterval();
        };
    }

    private IP discoverIp(DiscoveryMethod discoveryMethod) {
        switch (discoveryMethod) {
            case STUN:

        }
        return null;
    }
}
