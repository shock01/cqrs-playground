package nl.stefhock.auth.app.application.registrations.strategies;

import nl.stefhock.auth.app.application.registrations.queries.RegistrationsQuery;

import javax.inject.Inject;

public class EmailExistsStrategy {

    private final RegistrationsQuery registrationsQuery;

    @Inject
    public EmailExistsStrategy(RegistrationsQuery registrationsQuery) {
        this.registrationsQuery = registrationsQuery;
    }

    public boolean exists(String email) {
        return registrationsQuery.byEmail(email).isPresent();
    }
}
