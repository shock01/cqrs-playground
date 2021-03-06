package nl.stefhock.auth.app.application.registrations.commandhandlers;

import nl.stefhock.auth.app.application.registrations.strategies.EmailExistsStrategy;
import nl.stefhock.auth.app.application.registrations.strategies.PasswordStrategy;
import nl.stefhock.auth.app.domain.aggregates.Registration;
import nl.stefhock.auth.app.domain.commands.CreateRegistration;
import nl.stefhock.auth.cqrs.application.CommandHandler;
import nl.stefhock.auth.cqrs.domain.Id;
import nl.stefhock.auth.cqrs.infrastructure.AggregateRepository;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Optional;
import java.util.Set;

/**
 * Created by hocks on 5-7-2017.
 */
public class CreateRegistrationHandler extends CommandHandler<CreateRegistration> {

    private final AggregateRepository aggregateRepository;
    private final PasswordStrategy passwordStrategy;
    private final EmailExistsStrategy emailExistsStrategy;
    @Inject
    public CreateRegistrationHandler(final AggregateRepository aggregateRepository,
                                     final PasswordStrategy passwordStrategy,
                                     final EmailExistsStrategy emailExistsStrategy) {
        super(CreateRegistration.class);
        this.aggregateRepository = aggregateRepository;
        this.passwordStrategy = passwordStrategy;
        this.emailExistsStrategy = emailExistsStrategy;
    }

    @Override
    public void execute(CreateRegistration command) {

        if (emailExistsStrategy.exists(command.getEmail())) {
            return;
        }
        final Id id = Id.from(command.getUuid());
        final Registration registration = new Registration();
        registration.create(id, command.getEmail(), command.getSource());
        final String seed = passwordStrategy.seed();
        final int iterations = passwordStrategy.iterations();
        final Optional<String> hash = passwordStrategy.hash(command.getPassword(), seed, iterations);

        // @TODO handle not created password
        hash.ifPresent(value -> registration.setPassword(value, seed, iterations));
        aggregateRepository.save(registration);
    }
}
