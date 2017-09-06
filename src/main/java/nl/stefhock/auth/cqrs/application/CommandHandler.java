package nl.stefhock.auth.cqrs.application;

/**
 * Created by hocks on 5-7-2017.
 */
public abstract class CommandHandler<T extends Command> {

    private final Class<T> type;

    protected CommandHandler(Class<T> type) {
        this.type = type;
    }

    public abstract void execute(T command);

    public Class<T> getType() {
        return type;
    }
}
