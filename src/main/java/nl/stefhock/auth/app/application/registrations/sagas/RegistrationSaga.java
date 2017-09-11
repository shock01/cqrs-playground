package nl.stefhock.auth.app.application.registrations.sagas;

import nl.stefhock.auth.app.domain.events.RegistrationCreated;
import nl.stefhock.auth.cqrs.infrastructure.ReadModel;

public class RegistrationSaga {


    private final ReadModel<RegistrationSaga> readModel;

    RegistrationSaga(final ReadModel<RegistrationSaga> readModel) {
        this.readModel = readModel;
    }

    void when(RegistrationCreated registrationCreated) {

    }

}
