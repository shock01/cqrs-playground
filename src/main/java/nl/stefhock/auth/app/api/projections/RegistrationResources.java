package nl.stefhock.auth.app.api.projections;

import nl.stefhock.auth.app.application.Projections;
import nl.stefhock.auth.app.application.command.RegistrationCommand;
import nl.stefhock.auth.app.application.projection.RegistrationsProjection;
import nl.stefhock.auth.cqrs.application.CommandBus;
import nl.stefhock.auth.cqrs.domain.Id;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.executable.ValidateOnExecution;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by hocks on 5-7-2017.
 */
@Path("/registrations")
public class RegistrationResources {

    private final CommandBus commandBus;
    private final Projections.Registrations registrationsProjection;

    @Inject
    public RegistrationResources(final CommandBus commandBus,
                                 final Projections.Registrations registrationsProjection) {
        this.commandBus = commandBus;
        this.registrationsProjection = registrationsProjection;
    }

    /**
     * returns status see other with a link to the user registration
     * this is needed to be able to separate the projection from the commands
     *
     * @param registration
     * @return
     */
    @POST
    @ValidateOnExecution
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(@Valid final RegistrationCommand.RegistrationInfo registration,
                             @Context UriInfo uriInfo) {
        final String uuid = Id.generate();
        commandBus.execute(new RegistrationCommand(uuid, registration));
        final UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(String.format("byId/%s", uuid));
        return Response.created(builder.build()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response list(@PathParam("uuid") String uuid) {
        GenericEntity<List<RegistrationsProjection.Registration>> list = new GenericEntity<List<RegistrationsProjection.Registration>>(registrationsProjection.list()) {
        };
        return Response.ok().entity(list).build();
    }

    @Path("/account/{uuid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response byId(@PathParam("uuid") String uuid) {
        final Optional<RegistrationsProjection.Registration> registration = registrationsProjection.byId(uuid);
        if (registration.isPresent()) {
            return Response.ok().entity(registration.get()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("/email/{email}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response byEmail(@PathParam("email") String email) {
        final Optional<RegistrationsProjection.Registration> registration = registrationsProjection.byEmail(email);
        if (registration.isPresent()) {
            return Response.ok().entity(registration.get()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
