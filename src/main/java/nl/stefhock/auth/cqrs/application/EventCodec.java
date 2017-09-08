package nl.stefhock.auth.cqrs.application;


import nl.stefhock.auth.cqrs.domain.events.DomainEvent;
import nl.stefhock.auth.cqrs.domain.events.Event;

/**
 * Created by hocks on 20-7-2017.
 */
public interface EventCodec {
    byte[] encodeDomainEvent(DomainEvent event);

    DomainEvent decodeDomainEvent(byte[] data);

    byte[] encodeEvent(Event event);

    Event decodeEvent(byte[] data, Class<Event> type);
}
