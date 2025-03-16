package ddns.client.daemon;

public interface DaemonClient {

    void start();

    void startDetached();

    void stop();
}
