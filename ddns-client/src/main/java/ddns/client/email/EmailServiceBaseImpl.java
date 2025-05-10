package ddns.client.email;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

public class EmailServiceBaseImpl implements EmailService {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @SneakyThrows
    public void saveEmailInfo(String basePath, EmailInfo emailInfo) {
        basePath += "/config";
        File file = new File(basePath);
        if (!file.exists()) {
            boolean ignore = file.mkdirs();
        }

        basePath += "/email-config.json";
        File config = new File(basePath);
        if (!config.exists()) {
            boolean ignore = config.createNewFile();
        }

        String json = gson.toJson(emailInfo);
        try (FileWriter writer = new FileWriter(config, false)) {
            writer.write(json);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<EmailInfo> getEmailInfo(String basePath) {
        basePath += "/config";
        File file = new File(basePath);
        if (!file.exists()) {
            return Optional.empty();
        }

        basePath += "/email-config.json";
        File config = new File(basePath);
        if (!config.exists()) {
            return Optional.empty();
        }

        try (FileReader reader = new FileReader(config)) {
            EmailInfo emailInfo = gson.fromJson(reader, EmailInfo.class);
            return Optional.of(emailInfo);
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
