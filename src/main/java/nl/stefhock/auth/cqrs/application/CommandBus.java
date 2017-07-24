package nl.stefhock.auth.cqrs.application;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hocks on 5-7-2017.
 */
public class CommandBus {

    private Map<Class<? extends Command>, CommandHandler<? extends Command>> handlers;

    public CommandBus() {
        handlers = new HashMap<>();
    }

    public <T extends Command> CommandBus register(Class<T> command, CommandHandler<T> handler) {
        if (hasHandler(command)) {
            throw new RuntimeException(String.format("There is already a command handler registered for command: %s", command.getSimpleName()));
        }
        handlers.put(command, handler);
        return this;
    }

    public <T extends Command> void execute(T command) {
        if (!hasHandler(command.getClass())) {
            throw new RuntimeException(String.format("There is no command handler registered for command: %s", command.getClass().getSimpleName()));
        }
        final CommandHandler<T> handler = (CommandHandler<T>) handlers.get(command.getClass());
        handler.execute(command);
    }

    public boolean hasHandler(Class<? extends Command> command) {
        return handlers.containsKey(command);
    }
}
