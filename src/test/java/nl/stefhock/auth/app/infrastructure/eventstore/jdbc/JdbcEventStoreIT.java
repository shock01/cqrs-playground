//package nl.stefhock.auth.app.infrastructure.eventstore.jdbc;
//
//
//import nl.stefhock.auth.app.application.Application;
//import nl.stefhock.auth.app.domain.AuthEvent;
//import nl.stefhock.auth.app.domain.aggregates.Registration;
//import nl.stefhock.auth.cqrs.domain.Id;
//import nl.stefhock.auth.cqrs.domain.aggregates.Aggregate;
//import nl.stefhock.auth.cqrs.infrastructure.EntityConcurrencyException;
//import nl.stefhock.auth.cqrs.infrastructure.jdbc.postgresql.DbMigration;
//import nl.stefhock.auth.cqrs.infrastructure.jdbc.postgresql.PostgreSQLEventStore;
//import org.h2.jdbcx.JdbcDataSource;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import java.sql.SQLException;
//import java.util.List;
//
//import static junit.framework.TestCase.assertEquals;
//
///**
// * Created by hocks on 29-12-2016.
// */
//public class JdbcEventStoreIT {
//
//    static final String JDBC = "jdbc:h2:mem:match;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;MODE=PostgreSQL";
//    final String id = "withAggregateId";
//
//    private PostgreSQLEventStore subject;
//    private DbMigration dbMigration;
//    private JdbcDataSource dataSource;
//
//    @BeforeClass
//    public static void init() {
//        Application.init();
//    }
//
//    @Before
//    public void setUp() {
//        dataSource = new JdbcDataSource();
//        dataSource.setURL(JDBC);
//        subject = new PostgreSQLEventStore(dataSource, Application.aggregateFactory, Application.eventBus);
//        dbMigration = new DbMigration(JDBC);
//        dbMigration.migrate();
//    }
//
//    @After
//    public void cleanup() throws SQLException {
//        dataSource.getConnection()
//                .prepareStatement("DROP ALL OBJECTS")
//                .execute();
//    }
//
//    @Test
//    public void testSave() {
//        final Aggregate aggregate = new Registration(Id.from(id), "test");
//        subject.save(aggregate);
//        final Registration result = subject.byId(id, Registration.class);
//        assertEquals(aggregate.getId(), result.getId());
//    }
//
//    @Test(expected = EntityConcurrencyException.class)
//    public void testSaveConcurrency() {
//        final Aggregate aggregate = new Registration(Id.from(id), "test");
//        subject.save(aggregate);
//        subject.save(new Registration(Id.from(id), "test"));
//    }
//
//    @Test
//    public void getLastSequenceId() throws Exception {
//        assertEquals(0, subject.sequenceId());
//        final Aggregate aggregate = new Registration(Id.from(id), "test");
//        subject.save(aggregate);
//        assertEquals(1, subject.sequenceId());
//    }
//
//    @Test
//    public void getEvents() throws Exception {
//        subject.save(new Registration(Id.from(id), "test"));
//        subject.save(new Registration(Id.from("2"), "test"));
//        final List<AuthEvent> events = subject.getEvents(1, 1);
//        assertEquals(1, events.size());
//    }
//
//    @Test
//    public void getEventsOutOfBounds() throws Exception {
//        subject.save(new Registration(Id.from(id), "test"));
//        final List<AuthEvent> events = subject.getEvents(1, 10);
//        assertEquals(0, events.size());
//    }
//
//    @Test
//    public void getEventsForStream() throws Exception {
//        subject.save(new Registration(Id.from(id), "test"));
//        final List<AuthEvent> events = subject.getEventsForStream(id, 0, 1);
//        assertEquals(1, events.size());
//    }
//
//    @Test
//    public void getEventsForStreamOutOfBound() throws Exception {
//        subject.save(new Registration(Id.from(id), "test"));
//        final List<AuthEvent> events = subject.getEventsForStream(id, 1, 1);
//        assertEquals(0, events.size());
//    }
//}