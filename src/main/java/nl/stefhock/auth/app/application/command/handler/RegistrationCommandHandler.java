package nl.stefhock.auth.app.application.command.handler;

import nl.stefhock.auth.app.application.command.RegistrationCommand;
import nl.stefhock.auth.app.application.strategy.PasswordStrategy;
import nl.stefhock.auth.app.domain.model.Registration;
import nl.stefhock.auth.cqrs.application.CommandHandler;
import nl.stefhock.auth.cqrs.domain.Id;
import nl.stefhock.auth.cqrs.infrastructure.AggregateRepository;

import javax.inject.Inject;
import java.util.Optional;

/**
 * Created by hocks on 5-7-2017.
 */
public class RegistrationCommandHandler implements CommandHandler<RegistrationCommand> {

    private final AggregateRepository aggregateRepository;
    private final PasswordStrategy passwordStrategy;

    @Inject
    public RegistrationCommandHandler(final AggregateRepository aggregateRepository,
                                      final PasswordStrategy passwordStrategy) {
        this.aggregateRepository = aggregateRepository;
        this.passwordStrategy = passwordStrategy;
    }

    @Override
    public void execute(RegistrationCommand command) {
        final RegistrationCommand.RegistrationInfo registrationInfo = command.getRegistration();
        // can use the builder pattern here which is much stronger!!!
        final Id id = Id.from(command.getUuid());
        final Registration registration = new Registration();
        registration.create(id, registrationInfo.getEmail(), registrationInfo.getSource());
        final String seed = passwordStrategy.seed();
        int iterations = passwordStrategy.iterations();
        final Optional<String> hash = passwordStrategy.hash(registrationInfo.getPassword(), seed, iterations);

        // @TODO handle not created password
        hash.ifPresent(value -> registration.setPassword(value, seed, iterations));
        aggregateRepository.save(registration);

        // send out withEmail to user
        // application event -> async -> we can use RabbitMQ, ZeroMQ, whatever
        // broadcast system wide event
    }
}
