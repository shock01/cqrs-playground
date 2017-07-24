package nl.stefhock.auth.cqrs.application;

/**
 * Created by hocks on 5-7-2017.
 */
public interface CommandHandler<T extends Command> {

    void execute(T command);
}
