package nl.stefhock.auth.app.api;

import nl.stefhock.auth.cqrs.application.consistency.ConsistencyRegistry;
import nl.stefhock.auth.cqrs.application.consistency.ConsistencyStrategy;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by hocks on 17-7-2017.
 */
@Path("/statistics")
public class StatisticsResource {

    private final ConsistencyRegistry consistencyRegistry;

    @Inject
    public StatisticsResource(ConsistencyRegistry consistencyRegistry) {
        this.consistencyRegistry = consistencyRegistry;
    }

    @GET
    @Path("/strategies")
    @Produces(MediaType.APPLICATION_JSON)
    public Response list() {
        final GenericEntity<List<ConsistencyStrategy<?>>> list = new GenericEntity<List<ConsistencyStrategy<?>>>(consistencyRegistry.strategies()) {
        };
        return Response.ok().entity(list).build();
    }
}
