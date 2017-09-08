package nl.stefhock.auth.app.domain;

import com.google.inject.AbstractModule;
import nl.stefhock.auth.cqrs.application.EventCodec;
import nl.stefhock.auth.cqrs.domain.EventFactory;
import nl.stefhock.auth.cqrs.infrastructure.javax.json.JsonEventCodec;

/**
 * Created by hocks on 24-7-2017.
 */
public class DomainModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(EventFactory.class).to(JsonEventFactory.class);
        bind(EventCodec.class).to(JsonEventCodec.class);
    }
}
