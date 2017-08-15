package nl.stefhock.auth.app.application.projection;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.hazelcast.core.HazelcastInstance;
import nl.stefhock.auth.app.application.Projections;
import nl.stefhock.auth.cqrs.application.EventBus;
import nl.stefhock.auth.cqrs.application.consistency.ConsistencyBuilder;
import nl.stefhock.auth.cqrs.application.consistency.ConsistencyRegistry;
import nl.stefhock.auth.cqrs.application.consistency.ConsistencyStrategy;
import nl.stefhock.auth.cqrs.infrastructure.EventStore;
import nl.stefhock.auth.cqrs.infrastructure.ProjectionSource;
import nl.stefhock.auth.cqrs.infrastructure.projections.HazelcastProjectionSource;

/**
 * Created by hocks on 15-8-2017.
 */
public class RegistrationsProjectionModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(RegistrationsProjection.class).in(Scopes.SINGLETON);
    }

    @Provides
    @Singleton
    ProjectionSource<RegistrationsProjection.Registration> source(HazelcastInstance hazelcast) {
        final HazelcastProjectionSource<RegistrationsProjection.Registration> projectionSource = new HazelcastProjectionSource<>("registrations", hazelcast);
        projectionSource.initialize();
        return projectionSource;
    }

    @Provides
    @Singleton
    Projections.Registrations projection(ConsistencyBuilder builder,
                                         ConsistencyRegistry registry,
                                         EventBus eventBus,
                                         EventStore eventStore,
                                         RegistrationsProjection registrationsProjection) {

        final ConsistencyStrategy<Projections.Registrations> strategy = builder.of(Projections.Registrations.class)
                .named("registrations")
                .provider(registrationsProjection)
                .eventBus(eventBus)
                .eventStore(eventStore)
                .linear()
                .build();
        registry.register(strategy);
        return strategy.projection();
    }

}
