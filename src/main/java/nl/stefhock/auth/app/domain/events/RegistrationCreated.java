package nl.stefhock.auth.app.domain.events;

import nl.stefhock.auth.cqrs.domain.events.Event;

import javax.json.JsonObject;
import java.util.Optional;

/**
 * Created by hocks on 5-7-2017.
 */
public class RegistrationCreated extends Event {

    private String email;
    private String source;

    RegistrationCreated(Builder builder) {
        super(builder);
        email = builder.email;
        source = builder.source;
    }

    public static Builder builder(String aggregateId) {
        return (Builder) new Builder().aggregateId(aggregateId);
    }

    public String email() {
        return email;
    }

    public String source() {
        return source;
    }

    @Override
    public String toString() {
        return "RegistrationCreated{" +
                "email='" + email + '\'' +
                ", source='" + source + '\'' +
                "} " + super.toString();
    }

    @Override
    public JsonObject toJSON() {
        return jsonBuilder()
                .add("email", email)
                .add("source", Optional.ofNullable(source).orElse(""))
                .build();
    }

    public static class Builder extends Event.Builder {

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

        public Builder fromJson(JsonObject jsonObject) {
            super.fromJson(jsonObject);
            this.email = jsonObject.getString("email");
            this.source = jsonObject.getString("source");
            return this;
        }

        public RegistrationCreated build() {
            return new RegistrationCreated(this);
        }

    }
}
