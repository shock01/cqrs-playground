package nl.stefhock.auth.cqrs.application.consistency;

import nl.stefhock.auth.cqrs.application.EventBus;
import nl.stefhock.auth.cqrs.application.Projection;
import nl.stefhock.auth.cqrs.infrastructure.EventStore;

/**
 * Created by hocks on 14-7-2017.
 */
public class ConsistencyBuilder {


    private final ConsistencyRegistry registry;

    public ConsistencyBuilder(final ConsistencyRegistry registry) {
        this.registry = registry;
    }

    public <T extends Projection<?>> Builder<T> of(Class<T> query) {
        return new Builder<>(query, registry);
    }

    public static final class Builder<T extends Projection<?>> {

        private final Class<T> tClass;
        private T queryProvider;
        private ConsistencyRegistry registry;
        private EventBus eventBus;
        private EventStore eventStore;
        private String name;

        public Builder(Class<T> tClass, final ConsistencyRegistry registry) {
            this.tClass = tClass;
            this.registry = registry;
        }

        public Builder<T> eventBus(EventBus eventBus) {
            this.eventBus = eventBus;
            return this;
        }

        public Builder<T> eventStore(EventStore eventStore) {
            this.eventStore = eventStore;
            return this;
        }

        public Builder<T> named(String value) {
            name = value;
            return this;
        }


        public Builder<T> provider(T queryProvider) {
            this.queryProvider = queryProvider;
            return this;
        }

        /**
         * requires consistencyRegistry to be set otherwise it cannot build proxies
         * we can also consider making the registry a singleton then it will not
         * give any issues regarding needed dependencies
         * <p>
         * it sounds ok to have a registry as a singleton
         *
         */
        public Builder<T> linear() {
            return this;
        }

        public ConsistencyStrategy<T> build() {
            if (eventBus == null) {
                throw new IllegalArgumentException("eventBus not set on builder");
            }
            if (eventStore == null) {
                throw new IllegalArgumentException("Eventbus not set on builder");
            }
            if (queryProvider == null) {
                throw new IllegalArgumentException("QueryProvider not set on builder");
            }
            // @// FIXME: 17-7-2017  should have multiple strategies
            final ConsistencyStrategy<T> strategy = new LinearConsistencyStrategy(this);
            strategy.init();
            strategy.resume();
            return strategy;
        }


        T queryProvider() {
            return queryProvider;
        }

        EventBus eventBus() {
            return eventBus;
        }

        EventStore eventStore() {
            return eventStore;
        }

        public String name() {
            return name;
        }

        ConsistencyRegistry registry() {
            return registry;
        }

        Class<T> query() {
            return tClass;
        }
    }
}
