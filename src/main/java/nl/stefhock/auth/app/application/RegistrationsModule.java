package nl.stefhock.auth.app.application;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.hazelcast.core.HazelcastInstance;
import nl.stefhock.auth.app.application.queries.RegistrationView;
import nl.stefhock.auth.app.application.queries.RegistrationsQuery;
import nl.stefhock.auth.app.application.queryhandlers.RegistrationsQueryHandler;
import nl.stefhock.auth.cqrs.application.QueryRegistry;
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
    ReadModel<RegistrationView> readModel(HazelcastInstance hazelcast) {
        return HazelcastReadModel.factory("registrations", hazelcast);
    }

    @Provides
    @Singleton
    RegistrationsQuery query(
            ReadModel<RegistrationView> readModel,
            EventBus eventBus) {
        return new RegistrationsQueryHandler(readModel);
    }

    @Inject
    void subscribe(QueryRegistry handlers, RegistrationsQueryHandler handler, RegistrationsQuery query) {
        handlers.register(handler, query);
    }

}
