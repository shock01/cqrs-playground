package nl.stefhock.auth.app.infrastructure;

import nl.stefhock.auth.cqrs.application.EventBus;

/**
 * Created by hocks on 14-7-2017.
 */
public class GuavaEventBus implements EventBus {

    private final com.google.common.eventbus.EventBus eventBus;

    public GuavaEventBus() {
        this.eventBus = new com.google.common.eventbus.EventBus();
    }

    @Override
    public EventBus register(Object object) {
        eventBus.register(object);
        return this;
    }

    @Override
    public EventBus unregister(Object object) {
        eventBus.unregister(object);
        return this;
    }

    @Override
    public void post(Object event) {
        eventBus.post(event);
    }
}
