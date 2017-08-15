package nl.stefhock.auth.app.domain.events;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import nl.stefhock.auth.cqrs.domain.events.DomainEvent;

import java.util.Date;

/**
 * Created by hocks on 5-7-2017.
 */
@JsonDeserialize(builder = PasswordChangedEvent.Builder.class)
public class PasswordChangedEvent extends DomainEvent {
    private String hash;

    private String seed;

    private int iterations;

    private PasswordChangedEvent(String aggregateId,
                                 Date date) {
        super(aggregateId, date);
    }

    public static Builder builder(String aggregateId) {
        return new Builder().withAggregateId(aggregateId);
    }

    public String getHash() {
        return hash;
    }

    public String getSeed() {
        return seed;
    }

    public int getIterations() {
        return iterations;
    }

    /**
     * events should be immutable by design hence using builder pattern
     */
    public static class Builder extends DomainEvent.Builder<PasswordChangedEvent.Builder, PasswordChangedEvent> {

        private static final int ITERATIONS = 20000;

        Builder() {
            super(new PasswordChangedEvent(null, new Date()));
            event.iterations = ITERATIONS;
        }

        public Builder withHash(String password) {
            event.hash = password;
            return this;
        }

        public Builder withSeed(String seed) {
            event.seed = seed;
            return this;
        }

        public Builder withIterations(int iterations) {
            event.iterations = iterations;
            return this;
        }
    }
}
