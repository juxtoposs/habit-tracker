package lt.viko.eif.habittracker.exception;

/**
 * Exception thrown when a requested resource cannot be found.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Creates an exception with a custom message.
     *
     * @param message error message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Creates an exception for a missing resource by name and ID.
     *
     * @param resourceName name of the missing resource
     * @param id resource ID
     */
    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " with ID " + id + " was not found");
    }
}