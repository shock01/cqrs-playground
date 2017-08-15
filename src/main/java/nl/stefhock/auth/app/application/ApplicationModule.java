package nl.stefhock.auth.app.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import nl.stefhock.auth.app.application.command.RegistrationCommand;
import nl.stefhock.auth.app.application.command.handler.RegistrationCommandHandler;
import nl.stefhock.auth.cqrs.application.CommandBus;
import nl.stefhock.auth.cqrs.application.consistency.ConsistencyBuilder;
import nl.stefhock.auth.cqrs.application.consistency.ConsistencyRegistry;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

/**
 * Created by hocks on 24-7-2017.
 */
public class ApplicationModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(CommandBus.class).in(Singleton.class);
        bind(ConsistencyRegistry.class).in(Singleton.class);
        requestInjection(this);
    }

    @Provides
    @Singleton
    ConsistencyBuilder consistencyBuilder(ConsistencyRegistry registry) {
        return new ConsistencyBuilder(registry);
    }

    @Provides
    @Singleton
    public Configuration configuration(ObjectMapper objectMapper) throws IOException {
        return objectMapper.readValue(new File("conf/application.json"), Configuration.class);
    }

    @Inject
    void registrationCommandHandler(final CommandBus commandBus, final RegistrationCommandHandler registrationCommandHandler) {
        commandBus.register(RegistrationCommand.class, registrationCommandHandler);
    }

}
