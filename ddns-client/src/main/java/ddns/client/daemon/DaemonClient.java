package ddns.client.daemon;

import ddns.client.domain.Profile;

public interface DaemonClient {

    void start(String basePath);

    void addProfileToWork(Profile profile, String basePath);

    void removeProfileFromWork(String profileName, String basePath);
}
