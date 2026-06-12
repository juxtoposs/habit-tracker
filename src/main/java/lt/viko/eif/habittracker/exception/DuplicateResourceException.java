package lt.viko.eif.habittracker.exception;

/**
 * Exception thrown when a resource already exists and should not be duplicated.
 */
public class DuplicateResourceException extends RuntimeException {

    /**
     * Creates an exception with a custom message.
     *
     * @param message error message
     */
    public DuplicateResourceException(String message) {
        super(message);
    }
}