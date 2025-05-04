package ddns.client.daemon;

public interface DaemonClient {

    void start(String basePath);

    void stop();
}
