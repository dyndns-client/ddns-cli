package ddns.client.constant;

public class Defaults {

    public static final String USER_HOME_PATH = System.getProperty("user.home");

    public static final String DDNS_CLI_BASE_PATH = USER_HOME_PATH + "/.ddns-cli";

    public static final String DDNS_CLI_CONFIG_FILE_NAME = "ddns-config.json";

    public static final int DEFAULT_DISCOVERY_INTERVAL = 100;

    public static final int DEFAULT_DISCOVERY_SOCKET_TIMEOUT = 1000;

    public static final int DEFAULT_CUSTOM_PROVIDER_SOCKET_TIMEOUT = 1000;
}
