package ddns.client.daemon;

public class DaemonClientBaseImpl implements DaemonClient {

    @Override
    public void start() {
        System.out.println("starting ddns client...");
    }

    @Override
    public void startDetached() {
        System.out.println("starting ddns client...");
    }

    @Override
    public void stop() {
        System.out.println("stopping ddns client...");
    }
}
