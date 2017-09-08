package nl.stefhock.auth.app.domain.events;

import nl.stefhock.auth.cqrs.domain.events.Event;

import javax.json.JsonObject;

/**
 * Created by hocks on 5-7-2017.
 */
public class PasswordChanged extends Event {

    private String hash;
    private String seed;
    private int iterations;

    private PasswordChanged(Builder builder) {
        super(builder);
        hash = builder.hash;
        seed = builder.seed;
        iterations = builder.iterations;
    }

    public static Builder builder(String aggregateId) {
        return (Builder) new Builder().aggregateId(aggregateId);
    }

    public String hash() {
        return hash;
    }

    public String seed() {
        return seed;
    }

    public int iterations() {
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

    @Override
    public JsonObject toJSON() {
        return jsonBuilder()
                .add("hash", hash)
                .add("seed", seed)
                .add("iterations", iterations)
                .build();
    }


    public static class Builder extends Event.Builder {

        private static final int ITERATIONS = 20000;
        private int iterations;
        private String hash;
        private String seed;


        public Builder() {
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

        public Builder fromJson(JsonObject jsonObject) {
            super.fromJson(jsonObject);
            this.iterations = jsonObject.getInt("iterations");
            this.hash = jsonObject.getString("hash");
            this.seed = jsonObject.getString("seed");
            return this;
        }

        public PasswordChanged build() {
            return new PasswordChanged(this);
        }
    }
}
