package nl.stefhock.auth.app.domain.aggregates;

import nl.stefhock.auth.app.domain.events.PasswordChanged;
import nl.stefhock.auth.app.domain.events.RegistrationCreated;
import nl.stefhock.auth.app.domain.valueobjects.Password;
import nl.stefhock.auth.cqrs.domain.aggregates.Aggregate;
import nl.stefhock.auth.cqrs.domain.Id;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hocks on 5-7-2017.
 */
public class Registration extends Aggregate {

    private String email;
    private final List<Password> passwords = new ArrayList<>();
    private Password password;
    private String source;

    public void create(Id id, String email, String source) {
        final RegistrationCreated event = RegistrationCreated
                .builder(id.getValue())
                .email(email)
                .source(source)
                .build();
        publish(event);
    }

    /**
     * each registration should have its own seed to be safe for hash lookups and rainbow tables
     * should throw if password was already used last x times, domain logic
     *
     * @// TODO: 20-7-2017 verify if password was already used
     */
    public void setPassword(final String hash, final String seed, final int iteratios) {
        final PasswordChanged event = PasswordChanged.builder(id.getValue())
                .withHash(hash)
                .withSeed(seed)
                .withIterations(iteratios)
                .build();
        publish(event);
    }

    void when(RegistrationCreated event) {
        id = Id.from(event.getAggregateId());
        email = event.getEmail();
        source = event.getSource();
    }

    void when(PasswordChanged event) {

        final Password password = Password.builder()
                .withHash(event.getHash())
                .withIterations(event.getIterations())
                .withSeed(event.getSeed()).build();

        passwords.add(password);
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public List<Password> getPasswords() {
        return passwords;
    }

    public String getSource() {
        return source;
    }

    public Password getPassword() {
        return password;
    }
}
