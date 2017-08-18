package nl.stefhock.auth.cqrs.application;

import com.google.common.eventbus.Subscribe;
import nl.stefhock.auth.cqrs.domain.events.DomainEvent;
import nl.stefhock.auth.cqrs.infrastructure.EventStore;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by hocks on 17-8-2017.
 */
public class QueryRegistry {

    private static final Logger LOGGER = Logger.getLogger(QueryRegistry.class.getName());
    // @TODO make me configurable

    private static final int BATCH_SIZE = 10;

    private final Map<QueryHandler, Query> registars = new ConcurrentHashMap<>();

    private final BlockingQueue<Object> eventQueue = new LinkedBlockingQueue<>();

    private final EventStore eventStore;
    private final EventBus eventBus;
    private final Executor executor;
    private final Lock lock;

    @Inject
    public QueryRegistry(final EventStore eventStore, final EventBus eventBus, final Executor executor, final Lock lock) {
        this.eventStore = eventStore;
        this.eventBus = eventBus;
        this.executor = executor;
        this.lock = lock;
    }

    private static long getLowestSequence(Map<QueryHandler, Query> handlers) {
        return handlers.keySet()
                .stream()
                .reduce((a, b) -> {
                    if (a.readModel().sequenceInfo().sequenceId() < b.readModel().sequenceInfo().sequenceId()) {
                        return a;
                    } else {
                        return b;
                    }
                })
                .map(item -> item.readModel().sequenceInfo().sequenceId())
                .orElse(0L);

    }

    public void register(QueryHandler handler, Query query) {
        if (registars.containsKey(handler) && LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "handler already registered", handler);
        }
        registars.putIfAbsent(handler, query);
    }

    public void syncAll() {
        final long sequenceId = eventStore.sequenceId();
        if (!outOfSyncHandlers(sequenceId).isEmpty()) {
            syncBatched(sequenceId, BATCH_SIZE);
        }
    }

    public void listenForEvents() {
        executor.execute(Executors.defaultThreadFactory().newThread(new Consumer()));
        eventBus.register(this);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void when(DomainEvent event) {
        try {
            lock.lock();
            eventQueue.offer(event);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "registry is locked cannot queue event", e);
        }
    }

    private void syncBatched(long storeSequenceId, int limit) {
        // first make a map of items that are out of syncBatched and remove them from the set when
        // they are not needed anymore
        // this is becoming a little complex here now...do we really need this kinda logic here easier to do this just one by one
        try {
            lock.lock();
            final Map<QueryHandler, Query> handlers = outOfSyncHandlers(storeSequenceId);
            final long lowestSequence = getLowestSequence(handlers);
            syncBatched(handlers, storeSequenceId, lowestSequence, limit);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "cannot syncBatched events", e);
        } finally {
            lock.unlock();
        }
    }

    private void syncBatched(Map<QueryHandler, Query> handlers, long storeSequenceId, long querySequenceId, int limit) {
        long total = storeSequenceId - querySequenceId;
        for (long offset = querySequenceId; offset < total; offset += limit) {
            // chunk size = limit or the min of the total - start
            final long current = offset;
            final int chunkSize = (int) Math.min(limit, total - offset);

            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, String.format("fetching events offset:%d (limit:%d)", offset, chunkSize));

            }
            final List<DomainEvent> events = eventStore.getEvents(offset, chunkSize);
            handlers.entrySet()
                    .parallelStream()
                    .forEach(entry -> {
                        QueryHandler handler = entry.getKey();
                        int start = (int) ((handler.readModel().sequenceInfo().sequenceId() - current));
                        if (start < chunkSize) {
                            events.subList(start, chunkSize).stream().forEach(event -> EventDelegator.when(entry.getKey(), event));
                            // @todo current should come from event not from calculation
                            handler.readModel().synced(current + chunkSize);
                        }
                    });
        }

        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "readModels synced");
        }
    }

    private Map<QueryHandler, Query> outOfSyncHandlers(long storeSequenceId) {
        return registars.entrySet()
                .stream()
                .filter(item -> item.getKey().readModel().sequenceInfo().sequenceId() < storeSequenceId)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue));
    }

    private class Consumer implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    final Object event = eventQueue.take();
                    registars.values()
                            .parallelStream()
                            .forEach(handler -> EventDelegator.when(handler, event));
                } catch (InterruptedException e) {
                    if (LOGGER.isLoggable(Level.WARNING)) {
                        LOGGER.log(Level.WARNING, "cannot get event from queue", e);
                    }
                }
            }
        }
    }

}
