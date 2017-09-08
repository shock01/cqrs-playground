package nl.stefhock.auth.app.application;

import nl.stefhock.auth.cqrs.application.Registry;

import javax.inject.Inject;

/**
 * Created by hocks on 18-8-2017.
 */
public class Application {

    private final Registry registry;

    @Inject
    public Application(Registry registry) {
        this.registry = registry;
    }

    public void start() {
        registry.syncAll();
        registry.listenForEvents();
    }
}
