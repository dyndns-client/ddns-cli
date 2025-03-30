package ddns.client.config.component;

import dagger.Component;
import ddns.client.config.ClientConfig;
import ddns.client.config.GsonConfig;
import ddns.client.daemon.DaemonClient;
import jakarta.inject.Singleton;

@Component(modules = {GsonConfig.class, ClientConfig.class})
@Singleton
public interface ClientComponent {

    DaemonClient buildDaemonClient();
}
