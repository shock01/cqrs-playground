package nl.stefhock.auth.app.domain.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Created by hocks on 20-7-2017.
 * <p>
 * value object
 */
@JsonDeserialize(builder = Password.Builder.class)
public class Password {

    private String hash;
    private String seed;
    private int iterations;

    private Password() {

    }

    public static Builder builder() {
        return new Builder();
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

    static class Builder {

        private final Password password;

        Builder() {
            password = new Password();
        }

        public Builder withHash(String hash) {
            password.hash = hash;
            return this;
        }

        public Builder withSeed(String seed) {
            password.seed = seed;
            return this;
        }

        public Builder withIterations(int iterations) {
            password.iterations = iterations;
            return this;
        }

        public Password build() {
            return password;
        }
    }
}
