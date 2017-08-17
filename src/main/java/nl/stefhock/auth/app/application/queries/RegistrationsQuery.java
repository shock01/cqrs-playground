package nl.stefhock.auth.app.application.queries;

import nl.stefhock.auth.cqrs.application.Query;

import java.util.List;
import java.util.Optional;

/**
 * Created by hocks on 17-7-2017.
 * <p>
 * these interfaces are needed to be able to add MethodInvocationInterception
 * so that queries can be synchronized
 */

// @fixme is this generic extends still needed?

public interface RegistrationsQuery extends Query {

    List<RegistrationView> list();

    Optional<RegistrationView> byId(String uuid);

    Optional<RegistrationView> byEmail(String email);

}
