package nl.stefhock.auth.app.infrastructure.eventstore.jdbc;

import com.zaxxer.hikari.HikariDataSource;

import java.util.Optional;
import java.util.Properties;

/**
 * Created by hocks on 17-7-2017.
 */
public class DB {


    public static String jdbcUrl() {

//        jdbcUrl = Optional.ofNullable(System.getenv("JDBC_URL"))
//                .orElse("jdbc:h2:file:./data/db;DATABASE_TO_UPPER=false;MODE=PostgreSQL");
//
        return "jdbc:h2:mem:match;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;MODE=PostgreSQL";
    }

    public static javax.sql.DataSource dataSource() {
        final String driverClassName = Optional.ofNullable(System.getenv("DRIVER_CLASS_NAME"))
                .orElse("org.h2.Driver");


        final HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setMinimumIdle(2);
        final Properties properties = new Properties();
        properties.put("dataSource.cachePrepStmts", "true");
        properties.put("dataSource.useServerPrepStmts", "true");
        properties.put("dataSource.prepStmtCacheSize", "250");
        properties.put("dataSource.prepStmtCacheSqlLimit", "2048");
        hikariDataSource.setDataSourceProperties(properties);
        hikariDataSource.setDriverClassName(driverClassName);
        hikariDataSource.setJdbcUrl(jdbcUrl());
        return hikariDataSource;
    }
}
