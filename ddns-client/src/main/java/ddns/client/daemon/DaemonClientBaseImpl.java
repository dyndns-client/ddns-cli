package ddns.client.daemon;

import ddns.client.discovery.DnsDiscoveryService;
import ddns.client.discovery.HttpDiscoveryService;
import ddns.client.discovery.StunDiscoveryService;
import ddns.client.domain.DiscoveryMethod;
import ddns.client.domain.IP;
import ddns.client.domain.Profile;
import ddns.client.exception.DiscoveryException;
import ddns.client.profile.ProfileClient;
import ddns.client.profile.ProfileLogger;
import ddns.client.updater.IpUpdater;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DaemonClientBaseImpl implements DaemonClient {

    private final ProfileClient profileClient;
    private final StunDiscoveryService stunDiscoveryService;
    private final DnsDiscoveryService dnsDiscoveryService;
    private final HttpDiscoveryService httpDiscoveryService;

    @SneakyThrows
    @Override
    public void start(String basePath) {
        List<Profile> profiles = profileClient.getProfiles(basePath);

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(profiles.size());
        for (Profile profile : profiles) {
            int delay = getDelay(profile);
            executor.scheduleWithFixedDelay(new Runnable() {
                private final IpUpdater updater = new IpUpdater(basePath);

                @Override
                public void run() {
                    try {
                        IP ip = discoverIp(profile);
                        update(ip);
                    } catch (DiscoveryException e) {
                        ProfileLogger.error(basePath, profile.getName(), e.getMessage());
                    }
                }

                private void update(IP currentIp) {
                    IP lastUpdateIp = profile.getLastUpdateIp();
                    if (Objects.nonNull(lastUpdateIp)) {
                        if (!currentIp.equals(lastUpdateIp)) {
                            ProfileLogger.info(basePath, profile.getName(), "IP changed from " + lastUpdateIp + " to " + currentIp);
                            boolean updated = updater.update(currentIp, profile);
                            if (updated) {
                                profile.setLastUpdateIp(currentIp);
                                profileClient.updateProfile(profile, basePath);
                            }
                        }
                    } else {
                        boolean updated = updater.update(currentIp, profile);
                        if (updated) {
                            profile.setLastUpdateIp(currentIp);
                            profileClient.updateProfile(profile, basePath);
                        }
                    }
                }
            }, 0, delay, TimeUnit.SECONDS);
        }
        boolean ignore = executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
    }

    private int getDelay(Profile profile) {
        DiscoveryMethod discoveryMethod = profile.getDiscoveryMethod();
        return switch (discoveryMethod) {
            case STUN -> profile.getStunDiscoveryInfo().getDiscoveryInterval();
            case DNS -> profile.getDnsDiscoveryInfo().getDiscoveryInterval();
            case HTTP -> profile.getHttpDiscoveryInfo().getDiscoveryInterval();
        };
    }

    private IP discoverIp(Profile profile) throws DiscoveryException {
        DiscoveryMethod discoveryMethod = profile.getDiscoveryMethod();
        return switch (discoveryMethod) {
            case STUN -> stunDiscoveryService.discover(
                    profile.getIpVersion(),
                    profile.getStunDiscoveryInfo().getServer(),
                    profile.getStunDiscoveryInfo().getSocketTimeout()
            );
            case DNS -> dnsDiscoveryService.discover(
                    profile.getIpVersion(),
                    profile.getDnsDiscoveryInfo().getServer(),
                    profile.getDnsDiscoveryInfo().getSocketTimeout()
            );
            case HTTP -> httpDiscoveryService.discover(
                    profile.getHttpDiscoveryInfo().getServer(),
                    profile.getHttpDiscoveryInfo().getSocketTimeout()
            );
        };
    }
}
