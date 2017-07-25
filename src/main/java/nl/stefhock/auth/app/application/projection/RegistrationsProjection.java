package nl.stefhock.auth.app.application.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.eventbus.Subscribe;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import nl.stefhock.auth.app.application.Projections;
import nl.stefhock.auth.app.domain.events.RegistrationCreatedEvent;
import nl.stefhock.auth.cqrs.application.BaseProjection;
import nl.stefhock.auth.cqrs.application.Consistency;
import nl.stefhock.auth.cqrs.infrastructure.ProjectionSource;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by hocks on 5-7-2017.
 * <p>
 * should store the registrationsQuery outside of the event stream
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
 * from the store and after that return the query.
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
 * thing is that we actually will need to newResourceConfig an interface RegistrationQuery ..... so that we can decorate it
 * <p>
 * RegistrationQuery ,
 * <p>
 * <p>
 * new ConsistentRegistrationsQuery(linearConsistencyStrategy, registrationsQuery) -> will always sync before execution
 * new ConsistentRegistrationsQuery(pollingConsistencyStrategy, registrationsQuery) -> will sync at a certain interval
 * new ConsistentRegistrationsQuery(eventBasedStrategy, registrationsQuery) -> will sync when a domain event is dispatched
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
public class RegistrationsProjection extends BaseProjection<RegistrationsProjection.Registration> implements Projections.Registrations {

    private static SortByEmail SORT_BY_EMAIL;

    static {
        SORT_BY_EMAIL = new SortByEmail();
    }

    @Inject
    public RegistrationsProjection(final ProjectionSource<Registration> projectionSource) {
        super(projectionSource);
    }

    @Override
    public Set<Registration> list() {
        try {
            return projectionSource().stream().sorted(SORT_BY_EMAIL).collect(Collectors.toSet());
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    @Override
    public Optional<Registration> byId(String uuid) {
        return projectionSource().stream().filter(value -> value.getUuid().contentEquals(uuid)).findFirst();
    }

    @Override
    public Optional<Registration> byEmail(String email) {
        return projectionSource().stream().filter(value -> value.getEmail().contentEquals(email)).findFirst();
    }

    // @// FIXME: 13-7-2017 change to when(RegistrationCreatedEvent e)
    @Subscribe
    public void registrationCreated(RegistrationCreatedEvent e) {
        projectionSource().addOrUpdate(new Registration(e.getEmail(), e.getDate(), e.getAggregateId()));
    }

    private static class SortByEmail implements Comparator<Registration> {

        @Override
        public int compare(Registration o1, Registration o2) {
            return o1.getEmail().compareTo(o2.getEmail());
        }
    }

    // may also use IdentifiedDataSerializable or Portable which need to be setup in InfrastructureModule.class
    // however the class is really small and might not need to be made this complex if needed it can always be added
    public static class Registration implements DataSerializable {

        @JsonProperty
        private Date registrationDate;
        @JsonProperty
        private String email;
        @JsonProperty
        private String uuid;

        public Registration() {

        }

        Registration(String email, Date registrationDate, String uuid) {
            this.email = email;
            this.registrationDate = registrationDate;
            this.uuid = uuid;
        }

        String getUuid() {
            return uuid;
        }

        String getEmail() {
            return email;
        }

        Date getRegistrationDate() {
            return registrationDate;
        }

        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
            out.writeUTF(uuid);
            out.writeUTF(email);
            out.writeLong(registrationDate.getTime());
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
            uuid = in.readUTF();
            email = in.readUTF();
            registrationDate = new Date(in.readLong());
        }
    }
}
