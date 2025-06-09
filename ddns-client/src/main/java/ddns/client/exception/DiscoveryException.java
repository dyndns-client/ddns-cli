package ddns.client.exception;

public class DiscoveryException extends Exception {

    public DiscoveryException() {
        super("Discovery failed");
    }

    public DiscoveryException(String message) {
        super(message);
    }
}
