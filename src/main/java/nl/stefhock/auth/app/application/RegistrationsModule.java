package nl.stefhock.auth.app.application;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.multibindings.ProvidesIntoSet;
import com.hazelcast.core.HazelcastInstance;
import nl.stefhock.auth.app.application.commandhandlers.CreateRegistrationHandler;
import nl.stefhock.auth.app.application.queries.RegistrationView;
import nl.stefhock.auth.app.application.queries.RegistrationsQuery;
import nl.stefhock.auth.app.application.queryhandlers.RegistrationsQueryHandler;
import nl.stefhock.auth.app.application.strategies.PasswordStrategy;
import nl.stefhock.auth.cqrs.application.CommandHandler;
import nl.stefhock.auth.cqrs.application.QueryRegistry;
import nl.stefhock.auth.cqrs.infrastructure.AggregateRepository;
import nl.stefhock.auth.cqrs.infrastructure.ReadModel;
import nl.stefhock.auth.cqrs.infrastructure.hazelcast.HazelcastReadModel;

import javax.inject.Inject;

/**
 * Created by hocks on 15-8-2017.
 */
public class RegistrationsModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(RegistrationsQueryHandler.class).in(Scopes.SINGLETON);
        requestInjection(this);
    }

    @Provides
    @Singleton
    @SuppressWarnings("unused")
    ReadModel<RegistrationView> readModel(HazelcastInstance hazelcast) {
        return HazelcastReadModel.factory("registrations", hazelcast);
    }

    @Provides
    @Singleton
    @SuppressWarnings("unused")
    RegistrationsQuery query(
            ReadModel<RegistrationView> readModel) {
        return new RegistrationsQueryHandler(readModel);
    }

    @Inject
    @SuppressWarnings("unused")
    void subscribe(QueryRegistry handlers, RegistrationsQueryHandler handler, RegistrationsQuery query) {
        handlers.register(handler, query);
    }


    @ProvidesIntoSet
    @SuppressWarnings("unused")
    CommandHandler registrationCommandHandler(final AggregateRepository aggregateRepository,
                                              final PasswordStrategy passwordStrategy) {
        return new CreateRegistrationHandler(aggregateRepository, passwordStrategy);
    }

}
