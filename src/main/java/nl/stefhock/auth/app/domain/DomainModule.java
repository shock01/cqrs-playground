package nl.stefhock.auth.app.domain;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import nl.stefhock.auth.cqrs.application.EventCodec;
import nl.stefhock.auth.cqrs.infrastructure.javax.json.JsonEventCodec;

/**
 * Created by hocks on 24-7-2017.
 */
public class DomainModule extends AbstractModule {
    @Override
    protected void configure() {

    }

    @Provides
    @Singleton
    @SuppressWarnings("unused")
    EventCodec eventCodec() {
        return new JsonEventCodec();
    }
}
