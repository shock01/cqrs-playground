package nl.stefhock.auth.cqrs.application;

public interface Sequential {
    long sequenceId();

    void synced(long sequenceId);
}
