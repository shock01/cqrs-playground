package nl.stefhock.auth.app.domain.events;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Created by hocks on 5-7-2017.
 */
@JsonDeserialize(builder = PasswordChanged.Builder.class)
public class PasswordChanged extends AuthEvent {
    private String hash;

    private String seed;

    private int iterations;

    private PasswordChanged(Builder builder) {
        super(builder);
        this.hash = builder.hash;
        this.seed = builder.seed;
        this.iterations = builder.iterations;
    }

    public static Builder builder(String aggregateId) {
        return (Builder) new Builder().aggregateId(aggregateId);
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
    public static class Builder extends AuthEvent.Builder {

        private static final int ITERATIONS = 20000;
        private int iterations;
        private String hash;
        private String seed;


        Builder() {
            this.iterations = ITERATIONS;
        }

        public Builder withHash(String hash) {
            this.hash = hash;
            return this;
        }

        public Builder withSeed(String seed) {
            this.seed = seed;
            return this;
        }

        public Builder withIterations(int iterations) {
            this.iterations = iterations;
            return this;
        }

        public PasswordChanged build() {
            return new PasswordChanged(this);
        }
    }
}
