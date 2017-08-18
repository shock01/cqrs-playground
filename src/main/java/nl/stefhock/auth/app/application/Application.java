package nl.stefhock.auth.app.application;

import nl.stefhock.auth.cqrs.application.QueryRegistry;

import javax.inject.Inject;

/**
 * Created by hocks on 18-8-2017.
 */
public class Application {

    private final QueryRegistry registry;

    @Inject
    public Application(QueryRegistry registry) {
        this.registry = registry;
    }

    public void start() {
        registry.syncAll();
        registry.listenForEvents();
    }
}
