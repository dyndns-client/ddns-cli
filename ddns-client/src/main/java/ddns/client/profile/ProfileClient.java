package ddns.client.profile;

import ddns.client.domain.Profile;
import ddns.client.exception.ValidationException;

import java.util.List;

public interface ProfileClient {

    List<Profile> getProfiles(String basePath) throws ValidationException;

    void addProfile(Profile profile, String basePath) throws ValidationException;

    void removeProfile(String name, String basePath);
}
