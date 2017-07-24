package nl.stefhock.auth.cqrs.infrastructure;

/**
 * Created by hocks on 27-12-2016.
 */
public class EntityExistsException extends RuntimeException {
    public EntityExistsException(String message) {
        super(message);
    }
}
