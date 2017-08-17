package nl.stefhock.auth.cqrs.infrastructure;

/**
 * Created by hocks on 27-12-2016.
 */
public class EntityStoreException extends RuntimeException {
    public EntityStoreException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
