package nl.stefhock.auth.app.domain.events;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import nl.stefhock.auth.cqrs.domain.events.EventPayload;

import java.util.Date;

/**
 * Created by hocks on 5-7-2017.
 */
@JsonDeserialize(builder = RegistrationCreated.Builder.class)
@JsonTypeName("registrationCreated")
public class RegistrationCreated implements EventPayload {
    private final String aggregateId;
    private final Date date;
    private String email;
    private String source;

    RegistrationCreated(Builder builder) {
        aggregateId = builder.aggregateId;
        date = builder.date;
        email = builder.email;
        source = builder.source;
    }

    public static Builder builder(String aggregateId) {
        return (Builder) new Builder().aggregateId(aggregateId);
    }

    public String getEmail() {
        return email;
    }

    public String getSource() {
        return source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegistrationCreated that = (RegistrationCreated) o;

        if (!aggregateId.equals(that.aggregateId)) return false;
        if (!date.equals(that.date)) return false;
        if (!email.equals(that.email)) return false;
        return source.equals(that.source);
    }

    @Override
    public int hashCode() {
        int result = aggregateId.hashCode();
        result = 31 * result + date.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + source.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "RegistrationCreated{" +
                "email='" + email + '\'' +
                ", source='" + source + '\'' +
                "} " + super.toString();
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public Date getDate() {
        return date;
    }

    /**
     * events should be immutable by design hence using builder pattern
     */
    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder extends EventPayload.Builder {

        private String email;
        private String source;

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder source(String source) {
            this.source = source;
            return this;
        }

        public RegistrationCreated build() {
            return new RegistrationCreated(this);
        }

    }
}
