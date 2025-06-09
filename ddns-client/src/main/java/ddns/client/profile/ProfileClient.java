package ddns.client.profile;

import ddns.client.domain.Profile;

import java.util.List;
import java.util.Optional;

public interface ProfileClient {

    Optional<Profile> getProfile(String name, String basePath);

    List<Profile> getProfiles(String basePath);

    void addProfile(Profile profile, String basePath);

    void updateProfile(Profile profile, String basePath);

    boolean removeProfile(String name, String basePath);

    List<String> getProfileLogs(String name, String basePath);
}
