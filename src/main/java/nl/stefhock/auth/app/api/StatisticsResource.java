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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
        final List<Strategy> list = consistencyRegistry.strategies().stream().map(Strategy::from).collect(Collectors.toList());
        final GenericEntity<List<Strategy>> entity = new GenericEntity<List<Strategy>>(list) {
        };
        return Response.ok().entity(entity).build();
    }

    private static class Strategy {

        private String id;
        private String state;
        private Long sequenceId;
        private Date sequenceDate;
        private String name;
        private long storeSequenceId;

        private Strategy() {

        }

        static Strategy from(ConsistencyStrategy<?> strategy) {
            final Strategy instance = new Strategy();
            instance.id = strategy.id();
            instance.storeSequenceId = strategy.eventStore().sequenceId();
            instance.name = strategy.instance().getClass().getSimpleName();
            instance.state = strategy.state().name();
            instance.sequenceId = strategy.projection().projectionSource().sequenceInfo().sequenceId();
            instance.sequenceDate = strategy.projection().projectionSource().sequenceInfo().date();
            return instance;
        }

        public String getId() {
            return id;
        }

        public String getState() {
            return state;
        }

        public Long getSequenceId() {
            return sequenceId;
        }

        public Date getSequenceDate() {
            return sequenceDate;
        }

        public String getName() {
            return name;
        }

        public long getStoreSequenceId() {
            return storeSequenceId;
        }
    }
}
