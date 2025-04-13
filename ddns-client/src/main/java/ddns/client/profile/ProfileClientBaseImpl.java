package ddns.client.profile;

import com.google.gson.Gson;
import ddns.client.domain.Profile;
import ddns.client.exception.ValidationException;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ProfileClientBaseImpl implements ProfileClient {

    private final Gson gson;

    @Override
    public List<Profile> getProfiles(String basePath) throws ValidationException {
        return List.of();
    }

    @Override
    public void addProfile(Profile profile, String basePath) throws ValidationException {

    }

    @Override
    public void removeProfile(String name, String basePath) {

    }
}
