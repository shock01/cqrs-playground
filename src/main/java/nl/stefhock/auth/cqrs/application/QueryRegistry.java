package nl.stefhock.auth.cqrs.application;

import com.google.common.eventbus.Subscribe;
import nl.stefhock.auth.cqrs.domain.events.DomainEvent;
import nl.stefhock.auth.cqrs.infrastructure.EventStore;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by hocks on 17-8-2017.
 * refactor this to be a EventListener Registry so that both Sagas as Handlers can be registered
 * it should use a marker interface to defined certain classes as EventListener
 * if we do that then there is also no need to manually subscribe them because we can inject them using quava
 *
 * so @Saga extends @EventListener
 * and @Query extends @EventListener
 * then this class will be EventListenerRegistry
 * QueryHandler is not correct then we need a new interface that returns the sequenceInfo because now it's
 * dependant on readModel
 *
 * @EventRegistar should have sequenceInfo then there is no need to have both interfaces??
 * and we can just use a set instead
 */
public class QueryRegistry {

    // @TODO the sync stuff should be in a different class, single purpose
    private static final Logger LOGGER = Logger.getLogger(QueryRegistry.class.getName());
    // @TODO make me configurable
    private static final int BATCH_SIZE = 10;

    private final Map<QueryHandler, Query> registrars = new ConcurrentHashMap<>();

    private final BlockingQueue<DomainEvent> eventQueue = new LinkedBlockingQueue<>();

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

    private static long oldestSequence(Map<QueryHandler, Query> handlers) {
        return handlers.keySet()
                .stream()
                .reduce(QueryRegistry::oldestQueryHandler)
                .map(item -> item.readModel().sequenceInfo().sequenceId())
                .orElse(0L);
    }

    private static QueryHandler oldestQueryHandler(QueryHandler a, QueryHandler b) {
        if (a.readModel().sequenceId() < b.readModel().sequenceId()) {
            return a;
        } else {
            return b;
        }
    }

    public void register(QueryHandler handler, Query query) {
        if (registrars.containsKey(handler) && LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "handler already registered", handler);
        }
        registrars.putIfAbsent(handler, query);
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
        // @TODO add logic to verify the event sequence id and get a lock
        try {
            // should this be added to a queue or not...or just handled.
            // the queue is not really needed but it will make sense to not make it
            // block
            eventQueue.offer(event);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "registry is locked cannot queue event", e);
        }
    }

    private void syncBatched(long storeSequenceId, int limit) {
        try {
            lock.lock();
            /**
             * after acquiring the lock validate again if there are any out of sync handlers
             * they might have been updated meanwhile
             */
            final Map<QueryHandler, Query> handlers = outOfSyncHandlers(storeSequenceId);
            syncBatched(handlers, storeSequenceId, oldestSequence(handlers), limit);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "cannot syncBatched events", e);
        } finally {
            lock.unlock();
        }
    }

    private void syncBatched(Map<QueryHandler, Query> handlers, long storeSequenceId, long sequenceId, int limit) {
        long total = storeSequenceId - sequenceId;
        for (long offset = sequenceId; offset < total; offset += limit) {
            final long current = offset;
            final int chunkSize = (int) Math.min(limit, total - offset);
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, String.format("fetching events offset:%d (limit:%d)", offset, chunkSize));
            }
            final List<DomainEvent> events = eventStore.getEvents(offset, chunkSize);
            handlers.keySet()
                    .parallelStream()
                    .forEach(entry -> syncHandler(entry, current, events));
        }

        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "syncBatch done");
        }
    }

    private void syncHandler(QueryHandler handler, long offset, List<DomainEvent> events) {
        final int size = events.size();
        final int start = (int) ((handler.readModel().sequenceId() - offset));
        if (start >= size) {
            return;
        }
        events.subList(start, size)
                .stream()
                .forEach(event -> EventDelegator.when(handler, event.getPayload()));
        handler.readModel().synced(offset + size);
    }

    private Map<QueryHandler, Query> outOfSyncHandlers(long storeSequenceId) {
        return registrars.entrySet()
                .stream()
                .filter(item -> item.getKey().readModel().sequenceId() < storeSequenceId)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue));
    }

    private class Consumer implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    final DomainEvent event = eventQueue.take();
                    Optional.ofNullable(tryDelegate(event))
                            .ifPresent(throwables -> delegateThrowables(throwables));
                } catch (InterruptedException e) {
                    if (LOGGER.isLoggable(Level.WARNING)) {
                        LOGGER.log(Level.WARNING, "cannot get event from queue", e);
                    }
                }
            }
        }

        private void delegateThrowables(Set<Throwable> throwables) {
            throwables.forEach(throwable -> registrars
                    .values()
                    .parallelStream()
                    .forEach(handler -> EventDelegator.when(handler, throwable)));
        }

        private Set<Throwable> tryDelegate(DomainEvent event) {
            // @todo lock each readmodel
            // and only apply it when their sequenceid is lower
            // filter the collection in the lock
            // use a double lock
            // make sure that the eventSource id is 1 below
            // otherwise notify that the eventQueue is out of sync.
            // when its out of sync it should be distributing an event
            // on a different queue not used for domain events
            // there is no need to lock on startup...
            // so we need a method to indicate that we need to lockEventProcessing
            // which can be called in listenForEvents
            final Set<Throwable> throwables = new CopyOnWriteArraySet<>();
            registrars.values()
                    .parallelStream()
                    .forEach(handler -> {
                        try {
                            EventDelegator.when(handler, event.getPayload());
                        } catch (Exception e) {
                            throwables.add(e.getCause());
                        }
                    });
            return throwables.isEmpty() ? null : throwables;
        }
    }

}
