package nl.stefhock.auth.cqrs.application;

import com.google.common.eventbus.Subscribe;
import nl.stefhock.auth.cqrs.domain.events.DomainEvent;
import nl.stefhock.auth.cqrs.infrastructure.EventStore;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class EventDispatcher {

    private static final Logger LOGGER = Logger.getLogger(EventDispatcher.class.getName());
    // @TODO make me configurable if needed
    private static final int BATCH_SIZE = 1000;

    private final BlockingQueue<DomainEvent> eventQueue = new LinkedBlockingQueue<>();

    private final EventStore eventStore;
    private final EventBus eventBus;
    private final Executor executor;
    private final Lock lock;
    private final Registry registry;

    EventDispatcher(final EventStore eventStore,
                    final EventBus eventBus,
                    final Executor executor,
                    final Registry registry) {

        this.eventStore = eventStore;
        this.eventBus = eventBus;
        this.executor = executor;
        this.lock = new ReentrantLock();
        this.registry = registry;

    }

    private static long oldest(Set<Sequential> handlers) {
        return handlers
                .stream()
                .reduce(EventDispatcher::compareSequenceId)
                .map(Sequential::sequenceId)
                .orElse(0L);
    }

    private static Sequential compareSequenceId(Sequential a, Sequential b) {
        return (a.sequenceId() < b.sequenceId()) ? a : b;
    }

    public void listenForEvents() {
        executor.execute(Executors.defaultThreadFactory().newThread(new EventDispatcher.Consumer()));
        eventBus.register(this);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void when(DomainEvent event) {
        // @TODO add logic to verify the create sequence id and get a lock
        try {
            // should this be added to a queue or not...or just handled.
            // the queue is not really needed but it will make sense to not make it
            // block
            eventQueue.offer(event);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "registry is locked cannot queue create", e);
        }
    }

    private void syncBatched(long storeSequenceId, int limit) {
        try {
            lock.lock();
            /**
             * after acquiring the lock validate again if there are any out of sync handlers
             * they might have been updated meanwhile
             */
            final Set<Sequential> handlers = outOfSync(storeSequenceId);
            syncBatched(handlers, storeSequenceId, oldest(handlers), limit);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "cannot syncBatched events", e);
        } finally {
            lock.unlock();
        }
    }

    private void syncBatched(Set<Sequential> sequentials, long storeSequenceId, long sequenceId, int limit) {
        long total = storeSequenceId - sequenceId;
        for (long offset = sequenceId; offset < total; offset += limit) {
            final long current = offset;
            final int chunkSize = (int) Math.min(limit, total - offset);
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, String.format("fetching events offset:%d (limit:%d)", offset, chunkSize));
            }
            final List<DomainEvent> events = eventStore.getEvents(offset, chunkSize);
            sequentials.parallelStream()
                    .forEach(entry -> sync(entry, current, events));
        }

        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "syncBatch done");
        }
    }

    private void sync(Sequential sequential, long offset, List<DomainEvent> events) {
        final int size = events.size();
        final int start = (int) ((sequential.sequenceId() - offset));
        if (start >= size) {
            return;
        }
        events.subList(start, size)
                .forEach(event -> EventDelegator.when(sequential, event.getPayload()));
        sequential.synced(offset + size);
    }

    public void syncAll() {
        final long sequenceId = eventStore.sequenceId();
        if (!outOfSync(sequenceId).isEmpty()) {
            syncBatched(sequenceId, BATCH_SIZE);
        }
    }

    // @fixme we can make this static by providing the queries
    private Set<Sequential> outOfSync(long sequenceId) {
        return registry.queries()
                .stream()
                .filter(item -> item.sequenceId() < sequenceId)
                .collect(Collectors.toSet());
    }

    private class Consumer implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    final DomainEvent event = eventQueue.take();
                    Optional.ofNullable(tryDelegate(event))
                            .ifPresent(this::delegateThrowables);
                } catch (InterruptedException e) {
                    if (LOGGER.isLoggable(Level.WARNING)) {
                        LOGGER.log(Level.WARNING, "cannot get create from queue", e);
                    }
                }
            }
        }

        private void delegateThrowables(Set<Throwable> throwables) {
            throwables.forEach(throwable -> registry.queries()
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
            // when its out of sync it should be distributing an create
            // on a different queue not used for domain events
            // there is no need to lock on startup...
            // so we need a method to indicate that we need to lockEventProcessing
            // which can be called in listenForEvents
            final Set<Throwable> throwables = new CopyOnWriteArraySet<>();
            registry.queries()
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
