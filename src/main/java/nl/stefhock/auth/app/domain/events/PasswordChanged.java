package nl.stefhock.auth.app.domain.events;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import nl.stefhock.auth.cqrs.domain.events.EventPayload;

import java.util.Date;

/**
 * Created by hocks on 5-7-2017.
 */
@JsonDeserialize(builder = PasswordChanged.Builder.class)
public class PasswordChanged implements EventPayload {
    private final String aggregateId;
    private final Date date;
    private String hash;
    private String seed;
    private int iterations;

    private PasswordChanged(Builder builder) {

        hash = builder.hash;
        seed = builder.seed;
        iterations = builder.iterations;
        aggregateId = builder.aggregateId;
        date = builder.date;
    }

    public static Builder builder(String aggregateId) {
        return (Builder) new Builder().aggregateId(aggregateId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PasswordChanged that = (PasswordChanged) o;

        if (iterations != that.iterations) return false;
        if (!aggregateId.equals(that.aggregateId)) return false;
        if (!date.equals(that.date)) return false;
        if (!hash.equals(that.hash)) return false;
        return seed.equals(that.seed);
    }

    @Override
    public int hashCode() {
        int result = aggregateId.hashCode();
        result = 31 * result + date.hashCode();
        result = 31 * result + hash.hashCode();
        result = 31 * result + seed.hashCode();
        result = 31 * result + iterations;
        return result;
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

    public String getAggregateId() {
        return aggregateId;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "PasswordChanged{" +
                "aggregateId='" + aggregateId + '\'' +
                ", date=" + date +
                ", hash='" + hash + '\'' +
                ", seed='" + seed + '\'' +
                ", iterations=" + iterations +
                '}';
    }

    /**
     * events should be immutable by design hence using builder pattern
     */
    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder extends EventPayload.Builder {

        private static final int ITERATIONS = 20000;
        private int iterations;
        private String hash;
        private String seed;


        Builder() {
            this.iterations = ITERATIONS;
        }

        public Builder hash(String hash) {
            this.hash = hash;
            return this;
        }

        public Builder seed(String seed) {
            this.seed = seed;
            return this;
        }

        public Builder iterations(int iterations) {
            this.iterations = iterations;
            return this;
        }

        public PasswordChanged build() {
            return new PasswordChanged(this);
        }
    }
}
