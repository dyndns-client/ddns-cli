package ddns.client.profile;

import com.google.gson.Gson;
import ddns.client.domain.Profile;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ProfileClientBaseImpl implements ProfileClient {

    private final Gson gson;

    @Override
    public Optional<Profile> getProfile(String name, String basePath) {
        basePath += "/profiles";
        File file = new File(basePath + File.separator + name + ".json");
        if (!file.exists()) {
            return Optional.empty();
        }

        try (FileReader reader = new FileReader(file)) {
            Profile profile = gson.fromJson(reader, Profile.class);
            return Optional.of(profile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Profile> getProfiles(String basePath) {
        return List.of();
    }

    @SneakyThrows
    @Override
    public void addProfile(Profile profile, String basePath) {
        basePath += "/profiles";
        File dir = new File(basePath);
        if (!dir.exists()) {
            boolean ignore = dir.mkdirs();
        }

        File file = new File(basePath + File.separator + profile.getName() + ".json");
        if (!file.exists()) {
            boolean ignore = file.createNewFile();
        }

        String json = gson.toJson(profile);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(json);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeProfile(String name, String basePath) {

    }
}
