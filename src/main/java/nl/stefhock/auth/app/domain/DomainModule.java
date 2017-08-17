package nl.stefhock.auth.app.domain;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import nl.stefhock.auth.app.domain.events.AuthEvent;
import nl.stefhock.auth.cqrs.application.EventMapper;
import nl.stefhock.auth.cqrs.infrastructure.jackson.JacksonEventMapper;

/**
 * Created by hocks on 24-7-2017.
 */
public class DomainModule extends AbstractModule {
    @Override
    protected void configure() {

    }

    @Provides
    @Singleton
    EventMapper eventMapper() {
        return new JacksonEventMapper(AuthEvent.class);
    }
}
