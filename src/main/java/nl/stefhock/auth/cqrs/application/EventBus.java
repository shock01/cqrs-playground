package nl.stefhock.auth.cqrs.application;

import nl.stefhock.auth.cqrs.domain.events.DomainEvent;

/**
 * Created by hocks on 14-7-2017.
 */
public interface EventBus {
    EventBus register(Object object);

    EventBus unregister(Object object);

    void post(DomainEvent event);

}
