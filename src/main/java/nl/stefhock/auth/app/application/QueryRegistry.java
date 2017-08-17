package nl.stefhock.auth.app.application;

import nl.stefhock.auth.cqrs.application.EventBus;
import nl.stefhock.auth.cqrs.application.EventDelegator;
import nl.stefhock.auth.cqrs.application.Query;
import nl.stefhock.auth.cqrs.application.QueryHandler;
import nl.stefhock.auth.cqrs.domain.events.DomainEvent;
import nl.stefhock.auth.cqrs.infrastructure.EventStore;
import nl.stefhock.auth.cqrs.infrastructure.ReadModel;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by hocks on 17-8-2017.
 */
public class QueryRegistry {

    private static final Logger LOGGER = Logger.getLogger(QueryRegistry.class.getName());
    private final Map<QueryHandler, Query> registars = new ConcurrentHashMap<>();
    private final EventStore eventStore;
    private final EventBus eventBus;
    private final Executor executor;


    // @TODO needs a ExecutorService
    @Inject
    public QueryRegistry(final EventStore eventStore, final EventBus eventBus, final Executor executor) {
        this.eventStore = eventStore;
        this.eventBus = eventBus;
        this.executor = executor;
    }

    public void register(QueryHandler handler, Query query) {
        if (registars.containsKey(handler)) {
            // log something here
        }
        registars.putIfAbsent(handler, query);
    }

    public void syncAll() {
        final long sequenceId = eventStore.sequenceId();
        final long lowest = registars.keySet()
                .stream()
                .map(item -> item.readModel().sequenceInfo())
                .reduce((a, b) -> {
                    if (a.sequenceId() < b.sequenceId()) {
                        return a;
                    } else {
                        return b;
                    }
                })
                .map(ReadModel.SequenceInfo::sequenceId)
                .orElse(0L);
        if (lowest < sequenceId) {
            // create batches and apply then in parallel to the readModels
        }
        syncBatched(lowest, sequenceId, 1000);
    }

    public void listenForEvents() {
        eventBus.register(this);
    }

    // event listener
    public void when(DomainEvent event) {
        // TODO add some locking when we are syncing etc
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "handling event", event.getClass().getSimpleName());
        }
        registars.values()
                .parallelStream()
                .forEach(handler -> EventDelegator.when(handler, event));
    }

    private void syncBatched(long querySequenceId, long storeSequenceId, int limit) {
        // first make a map of items that are out of sync and remove them from the set when
        // they are not needed anymore
        try {
            int total = (int) (storeSequenceId - querySequenceId);
            for (int n = 0; n < total; n += limit) {
                int chunkSize = Math.min(total, n + limit);
                long offset = n + querySequenceId;
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.log(Level.INFO, String.format("fetching events %d (%d)", offset, chunkSize));
                }
                final List<DomainEvent> events = eventStore.getEvents(offset, chunkSize);
                events.stream().forEach(event -> when(event));
            }
            // update the sync info
            registars.keySet()
                    .parallelStream()
                    .forEach(handler -> handler.readModel().synced(storeSequenceId));
        } catch (Exception e) {
            //LOGGER.log(Level.SEVERE, "Exception syncing events", e);
        } finally {
            //lock.unlock();
            //this.resume();
        }
    }

}
