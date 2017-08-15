package nl.stefhock.auth.cqrs.infrastructure;


import nl.stefhock.auth.cqrs.domain.events.DomainEvent;

import java.util.List;

/**
 * Created by hocks on 14-7-2017.
 */
public interface EventStore {

    List<DomainEvent> getEventsForStream(String id, long afterSequence, int limit);

    List<DomainEvent> getEvents(long offset, int limit);

    long sequenceId();
}
