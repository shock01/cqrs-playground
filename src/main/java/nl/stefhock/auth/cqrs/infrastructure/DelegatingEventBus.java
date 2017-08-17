package nl.stefhock.auth.cqrs.infrastructure;

import nl.stefhock.auth.cqrs.application.EventBus;
import nl.stefhock.auth.cqrs.application.EventDelegator;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by hocks on 14-7-2017.
 */
public class DelegatingEventBus implements EventBus {

    private final Set<Object> listeners = new CopyOnWriteArraySet<>();

    @Override
    public EventBus register(Object object) {
        listeners.add(object);
        return this;
    }

    @Override
    public EventBus unregister(Object object) {
        if (listeners.contains(object)) {
            listeners.remove(object);
        }
        return this;
    }

    @Override
    public void post(Object event) {
        listeners.stream()
                .forEach(listener -> EventDelegator.when(listener, event));
    }
}
