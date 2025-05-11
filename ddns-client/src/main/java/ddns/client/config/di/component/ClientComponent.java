package ddns.client.config.di.component;

import dagger.Component;
import ddns.client.config.di.ClientConfig;
import ddns.client.config.di.GsonConfig;
import ddns.client.daemon.DaemonClient;
import ddns.client.email.EmailService;
import ddns.client.profile.ProfileClient;
import jakarta.inject.Singleton;

@Component(modules = {GsonConfig.class, ClientConfig.class})
@Singleton
public interface ClientComponent {

    DaemonClient buildDaemonClient();

    ProfileClient buildProfileClient();

    EmailService buildEmailService();
}
