package ddns.client.daemon;

import ddns.client.domain.Profile;

import java.util.concurrent.ExecutorService;

public interface DaemonClient {

    void start(String basePath);

    void addProfileToWork(Profile profile, String basePath);
}
