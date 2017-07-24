package nl.stefhock.auth.cqrs.application.consistency;

// @TODO break this dependency, or is it ok to only use guava :-) !!

import com.google.common.eventbus.Subscribe;
import nl.stefhock.auth.cqrs.application.Consistency;
import nl.stefhock.auth.cqrs.application.EventBus;
import nl.stefhock.auth.cqrs.application.Projection;
import nl.stefhock.auth.cqrs.domain.DomainEvent;
import nl.stefhock.auth.cqrs.domain.Id;
import nl.stefhock.auth.cqrs.infrastructure.EventStore;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by hocks on 14-7-2017.
 */
public abstract class ConsistencyStrategy<T extends Projection<?>> {

    private static final Logger LOGGER = Logger.getLogger(Consistency.class.getName());

    final T instance;
    private final EventBus eventBus;
    private final EventStore eventStore;
    private final String id;
    private List<DomainEvent> queue;
    volatile private State state;
    private String name;
    private T query;

    public ConsistencyStrategy(ConsistencyBuilder.Builder<T> builder, T query) {
        instance = builder.queryProvider();
        eventBus = builder.eventBus();
        eventStore = builder.eventStore();
        name = builder.name();
        queue = new ArrayList<>();
        this.query = query;
        state = State.UNINITIALIZED;
        id = Id.generate();
    }

    void init() {
        state = State.INITIALIZED;
    }

    synchronized ConsistencyStrategy<T> resume() {

        if (state != State.SYNCED) {
            if (state == State.SUSPENDED) {
                eventBus.unregister(this);
            }
            // repost events that were missed during sync
            eventBus.register(instance);
            queue.forEach(eventBus::post);
            state = State.SYNCED;
        }

        return this;
    }

    synchronized ConsistencyStrategy<T> suspend() {
        if (state == State.SYNCED) {
            eventBus.unregister(instance);
        }
        eventBus.register(this);
        state = State.SUSPENDED;

        return this;
    }

    @Subscribe
    public void handle(DomainEvent e) {
        this.queue.add(e);
    }

    public abstract void synchronize();

    synchronized void syncBatched(long querySequenceId, long storeSequenceId) {
        int limit = 1000;
        int total = (int) (storeSequenceId - querySequenceId);
        for (int n = 0; n < total; n += limit) {
            int chunkSize = Math.min(total, n + limit);
            long offset = n + querySequenceId;
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info(String.format("[%s] getting events from store: offset:%d, limit:%d", this, offset, chunkSize));
            }
            eventStore.getEvents(offset, chunkSize)
                    .stream()
                    .forEach(eventBus::post);
        }
        query.projectionSource().sequenceInfo().update(storeSequenceId);
    }


    synchronized long eventStoreSequenceId() {
        return eventStore.sequenceId();
    }

    public String id() {
        return id;
    }

    public T query() {
        return query;
    }

    public State state() {
        return state;
    }

    public T instance() {
        return instance;
    }

    public EventStore eventStore() {
        return eventStore;
    }

    @Override
    public String toString() {
        return "ConsistencyStrategy{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", state=" + state +
                '}';
    }

    public enum State {
        UNINITIALIZED,
        INITIALIZED,
        SYNCED,
        SUSPENDED
    }


}
