package nl.stefhock.auth.app.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import nl.stefhock.auth.app.application.commandhandlers.CreateRegistrationHandler;
import nl.stefhock.auth.app.domain.commands.CreateRegistration;
import nl.stefhock.auth.cqrs.application.CommandBus;
import nl.stefhock.auth.cqrs.application.EventBus;
import nl.stefhock.auth.cqrs.infrastructure.EventStore;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hocks on 24-7-2017.
 */
public class ApplicationModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(CommandBus.class).in(Singleton.class);
        requestInjection(this);
    }

    @Provides
    @Singleton
    QueryRegistry queryRegistry(EventBus eventBus, EventStore eventStore) {
        final ExecutorService executor = Executors.newFixedThreadPool(4);
        final QueryRegistry registry = new QueryRegistry(eventStore, eventBus, executor);
        return registry;
    }


    @Provides
    @Singleton
    Configuration configuration(ObjectMapper objectMapper) throws IOException {
        return objectMapper.readValue(new File("conf/application.json"), Configuration.class);
    }

    @Inject
    void registrationCommandHandler(final CommandBus commandBus, final CreateRegistrationHandler createRegistrationHandler) {
        commandBus.register(CreateRegistration.class, createRegistrationHandler);
    }

}
