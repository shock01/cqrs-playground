package nl.stefhock.auth.cqrs.domain;

import java.util.UUID;

/**
 * Created by hocks on 30-12-2016.
 */
public class Id {

    private final String value;

    public Id(final String value) {
        this.value = value;
    }

    public Id() {
        this(UUID.randomUUID().toString());
    }

    public static Id from(String id) {
        return new Id(id);
    }

    public static String generate() {
        return UUID.randomUUID().toString();
    }

    public String getValue() {
        return value;
    }
}
