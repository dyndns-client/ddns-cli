package ddns.client.profile;

import ddns.client.domain.Profile;
import ddns.client.exception.ValidationException;

import java.util.List;
import java.util.Optional;

public interface ProfileClient {

    Optional<Profile> getProfile(String name, String basePath);

    List<Profile> getProfiles(String basePath);

    void addProfile(Profile profile, String basePath) throws ValidationException;

    void removeProfile(String name, String basePath);
}
