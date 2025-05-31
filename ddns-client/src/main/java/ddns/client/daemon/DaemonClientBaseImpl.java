package ddns.client.daemon;

import ddns.client.discovery.DnsDiscoveryService;
import ddns.client.discovery.HttpDiscoveryService;
import ddns.client.discovery.StunDiscoveryService;
import ddns.client.domain.Profile;
import ddns.client.email.EmailService;
import ddns.client.profile.ProfileClient;
import ddns.client.utils.ProfileUtils;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

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
    private final EmailService emailService;

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);

    @Override
    public void start(String basePath) {
        List<Profile> profiles = profileClient.getProfiles(basePath);

        for (Profile profile : profiles) {
            int delay = ProfileUtils.getDelay(profile);
            executor.scheduleWithFixedDelay(
                    new DaemonProfileTask(
                            profileClient, stunDiscoveryService, dnsDiscoveryService, httpDiscoveryService, emailService, basePath, profile
                    ), 0, delay, TimeUnit.SECONDS);
        }
    }

    @Override
    public void addProfileToWork(Profile profile, String basePath) {
        int delay = ProfileUtils.getDelay(profile);
        executor.scheduleWithFixedDelay(
                new DaemonProfileTask(
                        profileClient, stunDiscoveryService, dnsDiscoveryService, httpDiscoveryService, emailService, basePath, profile
                ), 0, delay, TimeUnit.SECONDS);
    }
}
