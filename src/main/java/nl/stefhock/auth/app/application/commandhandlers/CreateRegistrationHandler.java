package nl.stefhock.auth.app.application.commandhandlers;

import nl.stefhock.auth.app.domain.commands.CreateRegistration;
import nl.stefhock.auth.app.application.strategies.PasswordStrategy;
import nl.stefhock.auth.app.domain.aggregates.Registration;
import nl.stefhock.auth.cqrs.application.CommandHandler;
import nl.stefhock.auth.cqrs.domain.Id;
import nl.stefhock.auth.cqrs.infrastructure.AggregateRepository;

import javax.inject.Inject;
import java.util.Optional;

/**
 * Created by hocks on 5-7-2017.
 */
public class CreateRegistrationHandler implements CommandHandler<CreateRegistration> {

    private final AggregateRepository aggregateRepository;
    private final PasswordStrategy passwordStrategy;

    @Inject
    public CreateRegistrationHandler(final AggregateRepository aggregateRepository,
                                     final PasswordStrategy passwordStrategy) {
        this.aggregateRepository = aggregateRepository;
        this.passwordStrategy = passwordStrategy;
    }

    @Override
    public void execute(CreateRegistration command) {
        final CreateRegistration.RegistrationInfo registrationInfo = command.getRegistration();
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

        // MessageQueue.dispatch()


        // send out withEmail to user
        // application event -> async -> we can use RabbitMQ, ZeroMQ, whatever
        // broadcast system wide event
    }
}
