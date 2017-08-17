package nl.stefhock.auth.cqrs.application;


import nl.stefhock.auth.cqrs.domain.events.DomainEvent;

/**
 * Created by hocks on 20-7-2017.
 */
public interface EventMapper{
    byte[] toBytes(Object event);

    <T extends DomainEvent> T toEvent(byte[] data);
}
