package nl.stefhock.auth.cqrs.application;

import nl.stefhock.auth.cqrs.infrastructure.EventStore;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * registry is a facade that delegate to EventDispatcher
 * - registers queries
 * - registers commandHandlers
 */
public class Registry {

    private static final Logger LOGGER = Logger.getLogger(Registry.class.getName());

    private final Set<Sequential> queries = new CopyOnWriteArraySet<>();
    private final Set<CommandHandler> handlers = new CopyOnWriteArraySet<>();

    private final CommandBus commandBus;
    private final EventDispatcher eventDispatcher;

    @Inject
    public Registry(final CommandBus commandBus,
                    final EventStore eventStore,
                    final EventBus eventBus,
                    final Executor executor) {
        this.commandBus = commandBus;
        this.eventDispatcher = new EventDispatcher(eventStore, eventBus, executor, this);
    }


    public void register(Query query) {
        if (queries.contains(query) && LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "query already registered", query);
            return;
        }
        queries.add(query);
    }

    public void register(CommandHandler handler) {
        commandBus.register(handler.getType(), handler);
        handlers.add(handler);
    }

    public void syncAll() {
        eventDispatcher.syncAll();
    }

    public void listenForEvents() {
        eventDispatcher.listenForEvents();
    }

    Set<Sequential> queries() {
        return Collections.unmodifiableSet(queries);
    }
}
