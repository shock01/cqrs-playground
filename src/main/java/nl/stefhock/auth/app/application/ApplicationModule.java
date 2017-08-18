package nl.stefhock.auth.app.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.*;
import nl.stefhock.auth.app.application.commandhandlers.CreateRegistrationHandler;
import nl.stefhock.auth.app.domain.DomainModule;
import nl.stefhock.auth.app.domain.commands.CreateRegistration;
import nl.stefhock.auth.app.infrastructure.InfrastructureModule;
import nl.stefhock.auth.cqrs.application.CommandBus;
import nl.stefhock.auth.cqrs.application.EventBus;
import nl.stefhock.auth.cqrs.application.QueryRegistry;
import nl.stefhock.auth.cqrs.infrastructure.EventStore;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by hocks on 24-7-2017.
 */
public class ApplicationModule extends AbstractModule {
    public static Injector injector() {
        return Guice.createInjector(
                new ApplicationModule(),
                new DomainModule(),
                new InfrastructureModule(),
                new RegistrationsModule());
    }

    @Override
    protected void configure() {
        bind(CommandBus.class).in(Singleton.class);
        requestInjection(this);
    }

    @Provides
    @Singleton
    @SuppressWarnings("unused")
    QueryRegistry queryRegistry(EventBus eventBus, EventStore eventStore) {
        final ExecutorService executor = Executors.newFixedThreadPool(4);
        final Lock lock = new ReentrantLock();
        return new QueryRegistry(eventStore, eventBus, executor, lock);
    }

    @Provides
    @Singleton
    @SuppressWarnings("unused")
    Configuration configuration(ObjectMapper objectMapper) throws IOException {
        return objectMapper.readValue(new File("conf/application.json"), Configuration.class);
    }

    @Inject
    @SuppressWarnings("unused")
    void registrationCommandHandler(final CommandBus commandBus, final CreateRegistrationHandler createRegistrationHandler) {
        commandBus.register(CreateRegistration.class, createRegistrationHandler);
    }

}
