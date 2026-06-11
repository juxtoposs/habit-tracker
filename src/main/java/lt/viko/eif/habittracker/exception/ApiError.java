package lt.viko.eif.habittracker.exception;

import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

/**
 * Standard error response returned by the REST API.
 *
 * @param timestamp time when the error occurred
 * @param status HTTP status code
 * @param error HTTP status reason
 * @param message readable error message
 * @param path request path where the error occurred
 * @param fieldErrors validation errors for individual fields
 */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors
) {

    /**
     * Creates a normal API error without field-specific validation errors.
     *
     * @param status HTTP status
     * @param message error message
     * @param path request path
     * @return API error object
     */
    public static ApiError of(HttpStatus status, String message, String path) {
        return new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                Collections.emptyMap()
        );
    }

    /**
     * Creates an API error with field-specific validation errors.
     *
     * @param status HTTP status
     * @param message error message
     * @param path request path
     * @param fieldErrors validation errors
     * @return API error object
     */
    public static ApiError of(
            HttpStatus status,
            String message,
            String path,
            Map<String, String> fieldErrors
    ) {
        return new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                fieldErrors
        );
    }
}