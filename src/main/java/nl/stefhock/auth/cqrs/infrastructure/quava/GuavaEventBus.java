package nl.stefhock.auth.cqrs.infrastructure.quava;

import nl.stefhock.auth.cqrs.application.EventBus;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by hocks on 14-7-2017.
 */
public class GuavaEventBus implements EventBus {

    private final Set<Object> listeners = new CopyOnWriteArraySet<>();

    private final com.google.common.eventbus.EventBus bus = new com.google.common.eventbus.EventBus();

    @Override
    public EventBus register(Object object) {
        bus.register(object);
        return this;
    }

    @Override
    public EventBus unregister(Object object) {
        bus.unregister(object);
        return this;
    }

    @Override
    public void post(Object event) {
        bus.post(event);
    }
}
