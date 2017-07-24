package nl.stefhock.auth.app.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Created by hocks on 18-7-2017.
 */
@Path("/health")
public class HealthResource {

    @GET
    public Response health() {
        return Response.noContent().build();
    }
}
