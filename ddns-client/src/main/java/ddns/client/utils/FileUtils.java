package ddns.client.utils;

import lombok.experimental.UtilityClass;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static ddns.client.constant.Defaults.DDNS_CLI_BASE_PATH;
import static ddns.client.constant.Defaults.DDNS_CLI_CONFIG_FILE_NAME;
import static ddns.client.constant.Defaults.USER_HOME_PATH;

@UtilityClass
public class FileUtils {

    public static void checkUserHomeDirectory() throws FileNotFoundException {
        if (Objects.isNull(USER_HOME_PATH)) {
            throw new FileNotFoundException("User home directory does not exist!");
        }

        boolean userHomeDirectoryExists = Files.exists(Path.of(USER_HOME_PATH));
        if (!userHomeDirectoryExists) {
            throw new FileNotFoundException(USER_HOME_PATH + " directory does not exist!");
        }
    }

    public static void checkBaseDirectory() throws FileNotFoundException {
        boolean ddnsCliDirectoryExists = Files.exists(Path.of(DDNS_CLI_BASE_PATH));
        if (!ddnsCliDirectoryExists) {
            throw new FileNotFoundException(DDNS_CLI_BASE_PATH + " directory does not exist!");
        }
    }

    public static void checkConfigFile() throws FileNotFoundException {
        checkConfigFile(DDNS_CLI_BASE_PATH);
    }

    public static void checkConfigFile(String path) throws FileNotFoundException {
        boolean configFileExists = Files.exists(Path.of(path + '/' + DDNS_CLI_CONFIG_FILE_NAME));
        if (!configFileExists) {
            throw new FileNotFoundException("Config file does not exist!");
        }
    }
}
