package nl.stefhock.auth.app.domain.valueobjects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Password password = (Password) o;

        if (iterations != password.iterations) return false;
        if (!hash.equals(password.hash)) return false;
        return seed.equals(password.seed);

    }

    @Override
    public int hashCode() {
        int result = hash.hashCode();
        result = 31 * result + seed.hashCode();
        result = 31 * result + iterations;
        return result;
    }

    public static class Builder {

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
