package nl.stefhock.auth.cqrs.application;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by hocks on 15-8-2017.
 */
public class EventDelegator {

    private static final Logger LOGGER = Logger.getLogger(EventDelegator.class.getName());

    public static void when(Object instance, Object event) {
        final Method when;
        try {
            when = instance.getClass().getDeclaredMethod("when", event.getClass());
            when.setAccessible(true);
            when.invoke(instance, event);
        } catch (NoSuchMethodException e) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, String.format("No such method: when, %s", event.getClass()));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "cannot delegate event", e);
            throw new RuntimeException(e);
        }
    }
}
