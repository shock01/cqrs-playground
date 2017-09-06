package nl.stefhock.auth.cqrs.domain.aggregates;


import nl.stefhock.auth.cqrs.domain.Id;
import nl.stefhock.auth.cqrs.application.EventDelegator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hocks on 5-7-2017.
 */
public abstract class Aggregate {

    protected Id id;
    private int version;
    private List<Object> uncommittedEvents = new ArrayList<>();

    public Aggregate() {
    }

    protected void publish(Object event) {
        uncommittedEvents.add(event);
        apply(event);
    }


    void apply(Object event) {
        version++;
        try {
            EventDelegator.when(this, event);
        } catch (Exception e) {
            version--;
            throw e;
        }
    }

    public List<Object> getUncommittedEvents() {
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
