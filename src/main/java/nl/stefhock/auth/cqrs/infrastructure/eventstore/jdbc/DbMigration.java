package nl.stefhock.auth.cqrs.infrastructure.eventstore.jdbc;

import org.flywaydb.core.Flyway;

/**
 * Created by hocks on 10-1-2017.
 */

//@FIXME move the db.migrations stuff to a different package
public class DbMigration {

    private final String jdbcUrl;

    public DbMigration(final String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public static void main(String[] args) {
        new DbMigration(args[0]).migrate();
    }

    public void migrate() {
        final Flyway flyway = new Flyway();
        flyway.setLocations("classpath:cqrs/eventstore/migration");
        flyway.setDataSource(jdbcUrl, null, null);
        flyway.migrate();
    }

}
