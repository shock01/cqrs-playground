package nl.stefhock.auth.app.domain.events;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import nl.stefhock.auth.cqrs.domain.events.DomainEvent;

import java.util.Date;

/**
 * Created by hocks on 5-7-2017.
 */
@JsonDeserialize(builder = PasswordChanged.Builder.class)
public class PasswordChanged extends AuthEvent {
    private String hash;

    private String seed;

    private int iterations;

    private PasswordChanged(String aggregateId,
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

    @Override
    public String toString() {
        return "PasswordChanged{" +
                "hash='" + hash + '\'' +
                ", seed='" + seed + '\'' +
                ", iterations=" + iterations +
                "} " + super.toString();
    }

    /**
     * events should be immutable by design hence using builder pattern
     */
    public static class Builder extends DomainEvent.Builder<PasswordChanged.Builder, PasswordChanged> {

        private static final int ITERATIONS = 20000;

        Builder() {
            super(new PasswordChanged(null, new Date()));
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
