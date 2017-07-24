package nl.stefhock.auth.app.infrastructure.eventstore.jdbc;


import nl.stefhock.auth.cqrs.application.EventBus;
import nl.stefhock.auth.cqrs.domain.Aggregate;
import nl.stefhock.auth.cqrs.domain.AggregateFactory;
import nl.stefhock.auth.cqrs.domain.DomainEvent;
import nl.stefhock.auth.cqrs.domain.EventMapper;
import nl.stefhock.auth.cqrs.infrastructure.AggregateRepository;
import nl.stefhock.auth.cqrs.infrastructure.EntityConcurrencyException;
import nl.stefhock.auth.cqrs.infrastructure.EntityStoreException;
import nl.stefhock.auth.cqrs.infrastructure.EventStore;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by hocks on 29-12-2016.
 * <p>
 * a lot of this code like the pattern to verify the version may/can be moved to an abstraction if
 * multiple types of AggregateRepositories will be created
 */
public class JdbcEventStore implements AggregateRepository, EventStore {

    private static final Logger LOGGER = Logger.getLogger(JdbcEventStore.class.getName());

    private static final String SELECT_SQL = "SELECT * FROM events WHERE aggregateId=? AND aggregateType=? ORDER BY sequence";

    private static final String INSERT_SQL = "INSERT INTO events (aggregateId, aggregateType, eventType, version, data) VALUES (?, ?, ?, ?, ?)";

    private static final String SELECT_MAX_SQL = "SELECT MAX(version) FROM events WHERE aggregateId=? AND aggregateType=?";

    private static final String SELECT_SEQUENCE_ID = "SELECT max(sequence) FROM events";

    private static final String SELECT_EVENTS_SQL = "SELECT * FROM events ORDER BY sequence LIMIT ? OFFSET ?";

    private static final String SELECT_AGGREGATE_EVENTS_SQL = "SELECT * FROM events WHERE aggregateId=? AND version > ? ORDER BY sequence LIMIT ?";

    private final AggregateFactory factory;

    private final DataSource dataSource;

    private final EventBus eventBus;

    private final EventMapper eventMapper;

    @Inject
    public JdbcEventStore(final DataSource dataSource,
                          final AggregateFactory factory,
                          final EventMapper eventMapper,
                          final EventBus eventBus) {
        this.dataSource = dataSource;
        this.factory = factory;
        this.eventMapper = eventMapper;
        this.eventBus = eventBus;
    }

    private List<DomainEvent> resultSetToEvents(ResultSet resultSet) {
        final List<DomainEvent> events = new ArrayList<>();
        try {
            if (!resultSet.isBeforeFirst()) {
                return Collections.emptyList();
            }
            while (resultSet.next()) {
                final String eventType = resultSet.getString("eventType");
                final byte[] data = resultSet.getBytes("data");
                events.add(eventMapper.toEvent(data, Class.forName(eventType)));
            }
            return events;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(final Aggregate aggregate) {
        final String aggregateType = aggregate.getClass().getName();
        final String aggregateId = aggregate.getId();
        final List<DomainEvent> events = aggregate.getUncommittedEvents();
        int version = aggregate.getVersion() - events.size();

        verifyVersion(aggregate, events);

        try (final Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            try (final PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
                int sequence = version;
                for (final DomainEvent event : events) {
                    sequence = ++sequence;
                    statement.setString(1, aggregateId);
                    statement.setString(2, aggregateType);
                    statement.setString(3, event.getClass().getName());
                    statement.setInt(4, sequence);
                    statement.setBytes(5, eventMapper.toBytes(event));
                    statement.addBatch();
                }
                statement.executeBatch();
            }
            connection.commit();
            dispatchEvents(aggregate);
        } catch (Exception e) {
            //logger.severe(String.format("Cannot store entity%n%s", e.getMessage()));
            throw new EntityStoreException("Cannot store entity", e);
        }
    }

    private void dispatchEvents(final Aggregate aggregate) {
        aggregate.getUncommittedEvents().forEach(eventBus::post);
        aggregate.clearUncommittedEvents();
    }

    private void verifyVersion(final Aggregate aggregate, final List<DomainEvent> events) {
        try (Connection connection = dataSource.getConnection()) {
            final String aggregateType = aggregate.getClass().getName();
            final int originalVersion = aggregate.getVersion() - events.size() + 1;

            try (final PreparedStatement statement = connection.prepareStatement(SELECT_MAX_SQL)) {
                statement.setString(1, aggregate.getId());
                statement.setString(2, aggregateType);
                final ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    int foundVersion = resultSet.getInt(1);
                    if (foundVersion >= originalVersion) {
                        LOGGER.warning(String.format("Version conflict: %s(%s) ( %d >= %d)", aggregateType, aggregate.getId(), foundVersion, originalVersion));
                        throw new EntityConcurrencyException();
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T extends Aggregate> T byId(final String id, final Class<T> cls) {
        try (Connection connection = dataSource.getConnection()) {

            try (final PreparedStatement statement = connection.prepareStatement(SELECT_SQL)) {
                statement.setString(1, id);
                statement.setString(2, cls.getName());
                try (final ResultSet resultSet = statement.executeQuery()) {
                    final List<DomainEvent> events = resultSetToEvents(resultSet);
                    return factory.create(cls, events);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<DomainEvent> getEventsForStream(String id, long afterSequence, int limit) {
        List<DomainEvent> events;
        try (final Connection connection = dataSource.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement(SELECT_AGGREGATE_EVENTS_SQL)) {
                statement.setString(1, id);
                statement.setLong(2, afterSequence);
                statement.setInt(3, limit);
                try (final ResultSet resultSet = statement.executeQuery()) {
                    events = resultSetToEvents(resultSet);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(events).orElse(Collections.emptyList());
    }

    @Override
    public List<DomainEvent> getEvents(long offset, int limit) {
        List<DomainEvent> events;
        try (Connection connection = dataSource.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement(SELECT_EVENTS_SQL)) {
                statement.setInt(1, limit);
                statement.setLong(2, offset);
                try (final ResultSet resultSet = statement.executeQuery()) {
                    events = resultSetToEvents(resultSet);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(events).orElse(Collections.emptyList());
    }

    @Override
    public long sequenceId() {
        long result = 0;
        try (final Connection connection = dataSource.getConnection()) {

            try (final ResultSet resultSet = connection.prepareCall(SELECT_SEQUENCE_ID).executeQuery()) {
                resultSet.next();
                result = resultSet.getLong(1);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "cannot get last sequence id", e);
        }
        return result;
    }
}
