package nl.stefhock.auth.app.domain;

import com.google.inject.AbstractModule;
import nl.stefhock.auth.cqrs.domain.events.JacksonEventMapper;
import nl.stefhock.auth.cqrs.domain.events.EventMapper;

/**
 * Created by hocks on 24-7-2017.
 */
public class DomainModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(EventMapper.class).to(JacksonEventMapper.class);
    }
}
