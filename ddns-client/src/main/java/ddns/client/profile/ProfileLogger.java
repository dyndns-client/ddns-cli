package ddns.client.profile;

import lombok.SneakyThrows;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ProfileLogger {

    public static void info(String basePath, String profileName, String message) {
        log(LogLevel.INFO, basePath, profileName, message);
    }

    public static void error(String basePath, String profileName, String message) {
        log(LogLevel.ERROR, basePath, profileName, message);
    }

    @SneakyThrows
    public static void log(LogLevel logLevel, String basePath, String profileName, String message) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(logLevel)
                .append(" - ")
                .append(ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ssXXX")))
                .append(" - ")
                .append("Profile: ")
                .append(profileName)
                .append(", message: ")
                .append(message);
        System.out.println(messageBuilder);
        basePath += "/profiles";
        File file = new File(basePath + File.separator + profileName + ".logs");
        if (!file.exists()) {
            boolean ignore = file.createNewFile();
        }

        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write(messageBuilder.toString());
            writer.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public enum LogLevel {
        INFO,
        ERROR
    }
}
