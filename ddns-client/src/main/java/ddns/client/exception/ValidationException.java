package ddns.client.exception;

public class ValidationException extends Exception {

    public ValidationException() {
        super("Validation failed");
    }

    public ValidationException(String message) {
        super(message);
    }
}
