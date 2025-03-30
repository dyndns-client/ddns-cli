package ddns.client.profile;

import ddns.client.domain.Profile;
import ddns.client.exception.ValidationException;

import java.util.List;

public interface ProfileClient {

    List<Profile> getProfiles() throws ValidationException;

    List<Profile> getProfiles(String path) throws ValidationException;

    void addProfile(Profile profile) throws ValidationException;

    void addProfile(Profile profile, String path) throws ValidationException;

    void removeProfile(String name);

    void removeProfile(String name, String path);
}
