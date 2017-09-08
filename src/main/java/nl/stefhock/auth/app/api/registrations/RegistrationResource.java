package nl.stefhock.auth.app.api.registrations;

import com.fasterxml.jackson.annotation.JsonProperty;
import nl.stefhock.auth.app.domain.commands.CreateRegistration;
import nl.stefhock.auth.app.application.queries.RegistrationView;
import nl.stefhock.auth.app.application.queries.RegistrationsQuery;
import nl.stefhock.auth.cqrs.application.CommandBus;
import nl.stefhock.auth.cqrs.domain.Id;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.executable.ValidateOnExecution;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;

/**
 * Created by hocks on 5-7-2017.
 */
@Path("/registrations")
public class RegistrationResource {

    private final CommandBus commandBus;
    private final RegistrationsQuery registrationsQuery;

    @Inject
    public RegistrationResource(final CommandBus commandBus,
                                final RegistrationsQuery registrationsQuery) {
        this.commandBus = commandBus;
        this.registrationsQuery = registrationsQuery;
    }

    /**
     * returns status see other with a link to the user registration
     * this is needed to be able to separate the projection from the commands
     *
     * @param registration
     * @return
     */
    @POST
    //@ValidateOnExecution
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(final RegistrationInfo registration,
                             @Context UriInfo uriInfo) {
        final String uuid = Id.generate();
        commandBus.execute(new CreateRegistration(uuid, registration.getEmail(), registration.getPassword(), registration.getSource()));
        final UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(String.format("byId/%s", uuid));
        return Response.created(builder.build()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response list(@PathParam("uuid") String uuid) {
        GenericEntity<List<RegistrationView>> list = new GenericEntity<List<RegistrationView>>(registrationsQuery.list()) {
        };
        return Response.ok().entity(list).build();
    }

    @Path("/account/{uuid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response byId(@PathParam("uuid") String uuid) {
        final Optional<RegistrationView> registration = registrationsQuery.byId(uuid);
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
        final Optional<RegistrationView> registration = registrationsQuery.byEmail(email);
        if (registration.isPresent()) {
            return Response.ok().entity(registration.get()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    public static class RegistrationInfo {
        @NotNull(message = "email required")
        private final String email;

        private final String password;

        private final String source;

        /**
         * password is not required eg when a registration is done using a
         * social network like Google
         *
         * @param email
         * @param password
         * @todo registration source should be added
         */
        public RegistrationInfo(final @JsonProperty("email") String email,
                                final @JsonProperty("password") String password,
                                final @JsonProperty("source") String source) {
            this.email = email;
            this.password = password;
            this.source = source;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public String getSource() {
            return source;
        }
    }
}
