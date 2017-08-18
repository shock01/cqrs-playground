package nl.stefhock.auth.app.infrastructure;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.zaxxer.hikari.HikariDataSource;
import nl.stefhock.auth.cqrs.infrastructure.quava.GuavaEventBus;
import nl.stefhock.auth.cqrs.infrastructure.jdbc.postgresql.DbMigration;
import nl.stefhock.auth.app.application.Configuration;
import nl.stefhock.auth.cqrs.infrastructure.jdbc.postgresql.PostgreSQLEventStore;
import nl.stefhock.auth.cqrs.application.EventBus;
import nl.stefhock.auth.cqrs.infrastructure.AggregateRepository;
import nl.stefhock.auth.cqrs.infrastructure.EventStore;

import java.util.Properties;

/**
 * Created by hocks on 24-7-2017.
 */
public class InfrastructureModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(PostgreSQLEventStore.class).in(Singleton.class);
        bind(AggregateRepository.class).to(PostgreSQLEventStore.class);
        bind(EventBus.class).to(GuavaEventBus.class).in(Singleton.class);
        bind(EventStore.class).to(PostgreSQLEventStore.class);
    }

    @Provides
    @SuppressWarnings("unused")
    DbMigration dbMigration(Configuration configuration) {
        return new DbMigration(configuration.dataSource.jdbcUrl);
    }

    @Provides
    @Singleton
    @SuppressWarnings("unused")
    javax.sql.DataSource dataSource(Configuration configuration) {
        final Configuration.DataSource dataSourceConfiguration = configuration.dataSource;
        final HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDriverClassName(dataSourceConfiguration.driverClass);
        hikariDataSource.setJdbcUrl(dataSourceConfiguration.jdbcUrl);
        hikariDataSource.setMinimumIdle(4);
        hikariDataSource.setMaximumPoolSize(20);
        final Properties properties = new Properties();
        properties.put("dataSource.cachePrepStmts", true);
        properties.put("dataSource.useServerPrepStmts", true);
        properties.put("dataSource.prepStmtCacheSize", 256);
        properties.put("dataSource.prepStmtCacheSqlLimit", 2048);
        hikariDataSource.setDataSourceProperties(properties);

        return hikariDataSource;
    }

    @Provides
    @Singleton
    @SuppressWarnings("unused")
    public HazelcastInstance hazelcastInstance() {
        // https://blog.hazelcast.com/comparing-serialization-methods/
        final Config config = new Config();
        config.getNetworkConfig().setPort(5900);
        config.getNetworkConfig().setPortAutoIncrement(false);
//        config.getSerializationConfig().addDataSerializableFactory
//                (1, (int id) -> (id == RegistrationsQueryHandler.RegistrationView.ID) ? new RegistrationsQueryHandler.RegistrationView() : null);
        final NetworkConfig network = config.getNetworkConfig();
        JoinConfig join = network.getJoin();
        join.getMulticastConfig().setEnabled(false);
        join.getTcpIpConfig().setEnabled(true);

        return Hazelcast.newHazelcastInstance(config);
    }


}
