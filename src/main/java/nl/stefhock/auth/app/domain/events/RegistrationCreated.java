package nl.stefhock.auth.app.domain.events;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import nl.stefhock.auth.cqrs.domain.events.DomainEvent;

import java.util.Date;

/**
 * Created by hocks on 5-7-2017.
 */
@JsonDeserialize(builder = RegistrationCreated.Builder.class)
public class RegistrationCreated extends AuthEvent {
    private String email;
    private String source;

    private RegistrationCreated(String aggregateId,
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

    @Override
    public String toString() {
        return "RegistrationCreated{" +
                "email='" + email + '\'' +
                ", source='" + source + '\'' +
                "} " + super.toString();
    }

    /**
     * events should be immutable by design hence using builder pattern
     */
    public static class Builder extends DomainEvent.Builder<RegistrationCreated.Builder, RegistrationCreated> {

        Builder() {
            super(new RegistrationCreated(null, new Date()));
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
