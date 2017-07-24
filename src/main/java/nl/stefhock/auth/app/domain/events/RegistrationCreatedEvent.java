package nl.stefhock.auth.app.domain.events;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import nl.stefhock.auth.cqrs.domain.DomainEvent;

import java.util.Date;

/**
 * Created by hocks on 5-7-2017.
 */
@JsonDeserialize(builder = RegistrationCreatedEvent.Builder.class)
public class RegistrationCreatedEvent extends DomainEvent {
    private String email;
    private String source;

    private RegistrationCreatedEvent(String aggregateId,
                                     Date date) {
        super(aggregateId, date);
    }

    public static Builder builder(String aggregateId) {
        return new Builder().withAggregateId(aggregateId);
    }

    public String getEmail() {
        return email;
    }

    public String getSource() {
        return source;
    }

    /**
     * events should be immutable by design hence using builder pattern
     */
    public static class Builder extends DomainEvent.Builder<RegistrationCreatedEvent.Builder, RegistrationCreatedEvent> {

        Builder() {
            super(new RegistrationCreatedEvent(null, new Date()));
        }

        public Builder withEmail(String email) {
            event.email = email;
            return this;
        }

        public Builder withSource(String source) {
            event.source = source;
            return this;
        }
    }
}
