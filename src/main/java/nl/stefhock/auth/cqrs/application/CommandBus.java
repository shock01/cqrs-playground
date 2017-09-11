package nl.stefhock.auth.cqrs.application;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by hocks on 5-7-2017.
 *
 * @// TODO: 18-8-2017 use quava eventBuis for the CommandBus
 */
public class CommandBus {

    private final Map<Class<? extends Command>, CommandHandler<? extends Command>> handlers;
    private final Validator validator;

    public CommandBus(final Validator validator) {
        this.validator = validator;
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
        validate(command);
        final CommandHandler<T> handler = (CommandHandler<T>) handlers.get(command.getClass());
        handler.execute(command);
    }

    private <T extends Command> void validate(T command) {
        final Set<ConstraintViolation<T>> violations = validator.validate(command);
        if (!violations.isEmpty()) {
            // @todo can we just throw some throwable instead ? now its requires parsing the message....
            // or use a tab delimiteer
            final String message = violations.stream()
                    .map(item -> String.format("%s: %s", item.getPropertyPath(), item.getMessage()))
                    .collect(Collectors.joining("\n"));
            throw new ValidationException(message);
        }
    }

    public boolean hasHandler(Class<? extends Command> command) {
        return handlers.containsKey(command);
    }
}
