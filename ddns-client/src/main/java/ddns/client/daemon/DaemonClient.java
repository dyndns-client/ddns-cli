package ddns.client.daemon;

public interface DaemonClient {

    void start(String basePath);

    void startDetached(String basePath);

    void stop();
}
