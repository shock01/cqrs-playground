package nl.stefhock.auth.app.application.registrations;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.ProvidesIntoSet;
import com.hazelcast.core.HazelcastInstance;
import nl.stefhock.auth.app.application.registrations.commandhandlers.CreateRegistrationHandler;
import nl.stefhock.auth.app.application.registrations.queries.RegistrationView;
import nl.stefhock.auth.app.application.registrations.queries.RegistrationsQuery;
import nl.stefhock.auth.cqrs.application.CommandHandler;
import nl.stefhock.auth.cqrs.application.Query;
import nl.stefhock.auth.cqrs.infrastructure.ReadModel;
import nl.stefhock.auth.cqrs.infrastructure.hazelcast.HazelcastReadModel;

/**
 * Created by hocks on 15-8-2017.
 */
public class RegistrationsModule extends AbstractModule {
    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    @SuppressWarnings("unused")
    ReadModel<RegistrationView> readModel(HazelcastInstance hazelcast) {
        return HazelcastReadModel.factory("registrations", hazelcast);
    }

    @ProvidesIntoSet
    @SuppressWarnings("unused")
    Query query(RegistrationsQuery query) {
        return query;
    }

    @ProvidesIntoSet
    @SuppressWarnings("unused")
    CommandHandler registrationCommandHandler(final CreateRegistrationHandler handler) {
        return handler;
    }

}
