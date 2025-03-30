package ddns.client.config;

import com.google.gson.Gson;
import dagger.Module;
import dagger.Provides;
import ddns.client.daemon.DaemonClient;
import ddns.client.daemon.DaemonClientBaseImpl;
import ddns.client.discovery.StunDiscoveryService;

@Module
public class ClientConfig {

    @Provides
    public DaemonClient provideDaemonClient(Gson gson, StunDiscoveryService stunDiscoveryService) {
        return new DaemonClientBaseImpl(stunDiscoveryService, gson);
    }
}
