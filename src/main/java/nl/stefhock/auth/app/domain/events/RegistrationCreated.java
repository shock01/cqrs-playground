package nl.stefhock.auth.app.domain.events;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.Date;

/**
 * Created by hocks on 5-7-2017.
 */
@JsonDeserialize(builder = RegistrationCreated.Builder.class)
public class RegistrationCreated extends AuthEvent {
    private String email;
    private String source;

    private RegistrationCreated(Builder builder) {
        super(builder);
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
    public String toString() {
        return "RegistrationCreated{" +
                "email='" + email + '\'' +
                ", source='" + source + '\'' +
                "} " + super.toString();
    }

    /**
     * events should be immutable by design hence using builder pattern
     */
    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder extends AuthEvent.Builder {

        private String email;
        private String source;
        private Date date;

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
