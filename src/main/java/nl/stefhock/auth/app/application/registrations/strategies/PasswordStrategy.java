package nl.stefhock.auth.app.application.registrations.strategies;

import com.google.common.hash.HashCode;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by hocks on 20-7-2017.
 */
public class PasswordStrategy {


    private static final Logger LOGGER = Logger.getLogger(PasswordStrategy.class.getName());

    private static final String HMAC_SHA_1 = "PBKDF2WithHmacSHA1";

    private static final int ITERATIONS = 20000;

    public String seed() {
        return HashCode.fromBytes(SecureRandom.getSeed(32)).toString();
    }

    public Optional<String> hash(String value, String seed, int iterations) {
        String hash = null;
        final PBEKeySpec spec = new PBEKeySpec(value.toCharArray(), seed.getBytes(), iterations, 64 * 8);
        try {
            byte[] encoded = SecretKeyFactory.getInstance(HMAC_SHA_1).generateSecret(spec).getEncoded();
            hash = HashCode.fromBytes(encoded).toString();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Cannot generate hash", e);
            e.printStackTrace();
        }
        return Optional.ofNullable(hash);
    }

    public int iterations() {
        return ITERATIONS;
    }
}
