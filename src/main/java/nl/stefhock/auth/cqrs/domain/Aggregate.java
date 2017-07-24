package nl.stefhock.auth.cqrs.domain;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by hocks on 5-7-2017.
 */
public abstract class Aggregate {

    private static final Logger LOGGER = Logger.getLogger(Aggregate.class.getName());

    protected Id id;
    private int version;
    private List<DomainEvent> uncommittedEvents = new ArrayList<>();

    public Aggregate() {
    }

    protected void publish(DomainEvent event) {
        uncommittedEvents.add(event);
        apply(event);
    }


    void apply(Object event) {
        version++;
        try {
            when(this, event);
        } catch (Exception e) {
            version--;
            throw e;
        }
    }

    private void when(Object instance, Object event) {
        final Method when;
        try {
            when = instance.getClass().getDeclaredMethod("when", event.getClass());
            when.setAccessible(true);
            when.invoke(instance, event);
        } catch (NoSuchMethodException e) {
            LOGGER.warning(String.format("No such method: when, %s", event.getClass()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<DomainEvent> getUncommittedEvents() {
        return uncommittedEvents;
    }

    public void clearUncommittedEvents() {
        uncommittedEvents = new ArrayList<>();
    }

    public int getVersion() {
        return version;
    }

    public String getId() {
        return id.getValue();
    }
}
