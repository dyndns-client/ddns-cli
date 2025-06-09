package ddns.client.auth;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ddns.client.domain.Auth;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

public class AuthServiceImpl implements AuthService {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @SneakyThrows
    @Override
    public void saveAuth(Auth auth, String basePath) {
        basePath += "/config";
        File file = new File(basePath);
        if (!file.exists()) {
            boolean ignore = file.mkdirs();
        }

        basePath += "/auth-config.json";
        File config = new File(basePath);
        if (!config.exists()) {
            boolean ignore = config.createNewFile();
        }

        String json = gson.toJson(auth);
        try (FileWriter writer = new FileWriter(config, false)) {
            writer.write(json);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Auth> getAuth(String basePath) {
        basePath += "/config";
        File file = new File(basePath);
        if (!file.exists()) {
            return Optional.empty();
        }

        basePath += "/auth-config.json";
        File config = new File(basePath);
        if (!config.exists()) {
            return Optional.empty();
        }

        try (FileReader reader = new FileReader(config)) {
            Auth auth = gson.fromJson(reader, Auth.class);
            return Optional.of(auth);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean clear(String basePath) {
        basePath += "/config";
        File file = new File(basePath);
        if (!file.exists()) {
            return false;
        }

        basePath += "/auth-config.json";
        File config = new File(basePath);
        if (!config.exists()) {
            return false;
        }

        return config.delete();
    }
}
