package nl.stefhock.auth.app.domain.events;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import nl.stefhock.auth.cqrs.domain.events.DomainEvent;

import java.util.Date;

/**
 * Created by hocks on 16-8-2017.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class AuthEvent extends DomainEvent {
    AuthEvent(String aggregateId, Date date) {
        super(aggregateId, date);
    }
}
