package nl.stefhock.auth.app.application;

/**
 * Created by hocks on 23-7-2017.
 */
public class Configuration {

    public DataSource dataSource;

    public Configuration() {
    }

    public static class DataSource {
        public String driverClass;
        public String jdbcUrl;

        public DataSource() {
        }

    }
}
