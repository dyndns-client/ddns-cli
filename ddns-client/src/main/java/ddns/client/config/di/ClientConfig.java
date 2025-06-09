package ddns.client.config.di;

import com.google.gson.Gson;
import dagger.Module;
import dagger.Provides;
import ddns.client.daemon.DaemonClient;
import ddns.client.daemon.DaemonClientBaseImpl;
import ddns.client.discovery.DnsDiscoveryService;
import ddns.client.discovery.HttpDiscoveryService;
import ddns.client.discovery.StunDiscoveryService;
import ddns.client.email.EmailService;
import ddns.client.email.EmailServiceBaseImpl;
import ddns.client.profile.ProfileClient;
import ddns.client.profile.ProfileClientBaseImpl;

@Module
public class ClientConfig {

    @Provides
    public DaemonClient provideDaemonClient(ProfileClient profileClient,
                                            StunDiscoveryService stunDiscoveryService,
                                            DnsDiscoveryService dnsDiscoveryService,
                                            HttpDiscoveryService httpDiscoveryService,
                                            EmailService emailService) {
        return new DaemonClientBaseImpl(profileClient, stunDiscoveryService, dnsDiscoveryService, httpDiscoveryService, emailService);
    }

    @Provides
    public ProfileClient provideProfileClient(Gson gson) {
        return new ProfileClientBaseImpl(gson);
    }

    @Provides
    public EmailService provideEmailService(Gson gson) {
        return new EmailServiceBaseImpl(gson);
    }
}
