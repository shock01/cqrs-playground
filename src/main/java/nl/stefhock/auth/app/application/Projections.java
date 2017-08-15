package nl.stefhock.auth.app.application;

import nl.stefhock.auth.app.application.projection.RegistrationsProjection;
import nl.stefhock.auth.cqrs.application.Projection;
import nl.stefhock.auth.cqrs.application.consistency.Sync;

import java.util.List;
import java.util.Optional;

/**
 * Created by hocks on 17-7-2017.
 * <p>
 * these interfaces are needed to be able to add MethodInvocationInterception
 * so that queries can be synchronized
 */
public class Projections {

    public interface Registrations extends Projection<RegistrationsProjection.Registration> {
        @Sync
        List<RegistrationsProjection.Registration> list();

        @Sync
        Optional<RegistrationsProjection.Registration> byId(String uuid);

        @Sync
        Optional<RegistrationsProjection.Registration> byEmail(String email);
    }
}
