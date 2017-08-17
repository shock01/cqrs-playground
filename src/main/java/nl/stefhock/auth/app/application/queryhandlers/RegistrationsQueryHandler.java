package nl.stefhock.auth.app.application.queryhandlers;

import nl.stefhock.auth.app.domain.events.RegistrationCreated;
import nl.stefhock.auth.app.application.queries.RegistrationView;
import nl.stefhock.auth.app.application.queries.RegistrationsQuery;
import nl.stefhock.auth.cqrs.application.Consistency;
import nl.stefhock.auth.cqrs.application.QueryHandler;
import nl.stefhock.auth.cqrs.infrastructure.ReadModel;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by hocks on 5-7-2017.
 * <p>
 * should store the projection outside of the event stream
 * should listen to domain events
 * should rebuild
 * should have logic to pause events and handle them after reading the state
 * <p>
 * should have a addOrUpdate and tryDelete on a ProjectionWriter (interface)
 *
 * @// FIXME: 13-7-2017 how to know the last event id since last sync because AggregateRepository does
 * @// FIXME: 13-7-2017 implement syncing when events have passed (http://squirrel.pl/blog/2015/09/14/achieving-consistency-in-cqrs-with-linear-event-store/)
 * @// FIXME: 13-7-2017 add IQueryWriter and IQueryReader with a MemoryBased implementation (HashMap)
 * @// FIXME: 13-7-2017 could use aspects like sync async to determine if the eventStore should be read to sync
 * or that it can be eventually consistent or we can add an interface like @Sync so that we decorate it and first load the events
 * from the store and after that return the projection.
 * <p>
 * iniitially do not newResourceConfig an abstraction but just implement here and abstract later to be reused
 * better to not directly use the eventBus here but to use generic like in a aggregate.when(Event)
 * <p>
 * <p>
 * <p>
 * not have methods to get last eventSequenceId that is stored
 * <p>
 * Should we also implement the interface IEventStore ???? to get like an aggregate by id and event offset
 * and the total events
 * we can actually just decorate the whole thing and abstract the synchronization and have a generic event handler
 * that will dispatch again on the delegate
 * its like linear consistency is a decorator thing
 * <p>
 * linearConsistencyStrategy will hold a lock
 * linearConsistencyStrategy will read the last eventSequence id from the eventStore and will sync then
 * call the decorated model
 * <p>
 * linear consistency can also be changed by polling consistency etc etc
 * <p>
 * <p>
 * thing is that we actually will need to newResourceConfig an interface RegistrationsQuery ..... so that we can decorate it
 * <p>
 * RegistrationsQuery ,
 * <p>
 * <p>
 * new ConsistentRegistrationsQuery(linearConsistencyStrategy, projection) -> will always sync before execution
 * new ConsistentRegistrationsQuery(pollingConsistencyStrategy, projection) -> will sync at a certain interval
 * new ConsistentRegistrationsQuery(eventBasedStrategy, projection) -> will sync when a domain event is dispatched
 * <p>
 * can a viewModel listen to Aggregates out side of the system ?? or are these domain events....system wide events
 * or should aggregates never be connected .....because that will be really complex...they should be changed
 * by commands not domain events....because a command changes an event does not..maybe all events should also be
 * dispatched as a system event on an async eventbus....because then we can do other things....
 * maybe these events should be distributed over kafka or anything that is fast enough
 * maybe a event should be marked as a domain event...or should the command do this?? better to do in command
 * to prevent definite loop of event and event handling
 * <p>
 * <p>
 * add the projectionReader and projectionWriter interface
 * <p>
 * there should also be something like a snapshot later on what can be used to speed up evenything on load
 * <p>
 * can and may also return registrations for today...by filtering an array on registrationDate evey time a call is made
 * we can add a sorted set sorted on dates and if the first registrationDate is not from today we need to filter it
 */
@Consistency
public class RegistrationsQueryHandler extends QueryHandler<RegistrationView> implements RegistrationsQuery {

    private static SortByEmail SORT_BY_EMAIL;

    static {
        SORT_BY_EMAIL = new SortByEmail();
    }

    @Inject
    public RegistrationsQueryHandler(final ReadModel<RegistrationView> readModel) {
        super(readModel);
    }

    @Override
    public List<RegistrationView> list() {
        try {
            return readModel().stream().sorted(SORT_BY_EMAIL).collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    @Override
    public Optional<RegistrationView> byId(String uuid) {
        return readModel().stream().filter(value -> value.getUuid().contentEquals(uuid)).findFirst();
    }

    @Override
    public Optional<RegistrationView> byEmail(String email) {
        return readModel().stream().filter(value -> value.getEmail().contentEquals(email)).findFirst();
    }

    void when(RegistrationCreated e) {
        readModel().addOrUpdate(new RegistrationView(e.getEmail(), e.getDate(), e.getAggregateId()));
    }

    private static class SortByEmail implements Comparator<RegistrationView> {

        @Override
        public int compare(RegistrationView o1, RegistrationView o2) {
            return o1.getEmail().compareTo(o2.getEmail());
        }
    }
}
