/**
 * Created by hocks on 4-7-2017.
 */
package nl.stefhock.auth.app;

import com.google.inject.Injector;
import nl.stefhock.auth.app.application.Application;
import nl.stefhock.auth.app.application.ApplicationModule;
import nl.stefhock.auth.app.application.Server;
import nl.stefhock.auth.app.domain.commands.CreateRegistration;
import nl.stefhock.auth.cqrs.application.CommandBus;
import nl.stefhock.auth.cqrs.domain.Id;
import nl.stefhock.auth.cqrs.infrastructure.jdbc.postgresql.DbMigration;
import org.slf4j.bridge.SLF4JBridgeHandler;

import static nl.stefhock.auth.app.infrastructure.jersey.JerseyResourceConfig.newResourceConfig;

public class Main {


    public static void main(String[] args) throws Exception {

        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        final Injector injector = ApplicationModule.injector();
        final Server server = new Server(9000, newResourceConfig(injector));
        final Application application = injector.getInstance(Application.class);
        // @todo this should be moved to another docker container
        injector.getInstance(DbMigration.class).migrate();

        final CommandBus commandBus = injector.getInstance(CommandBus.class);
        int i = 12;
        while (false && i-- > 0) {
            final CreateRegistration.RegistrationInfo info = new CreateRegistration.RegistrationInfo(String.format("%d@greetz.com", i), String.format("%d@greetz.com", i), null);
            commandBus.execute(new CreateRegistration(Id.generate(), info));
        }
        // past events
        application.start();

        // dummy things
        // new events
        i = 9;
        while (false && i-- > 0) {
            final CreateRegistration.RegistrationInfo info = new CreateRegistration.RegistrationInfo(String.format("%d@greetz.com", i), String.format("%d@greetz.com", i), null);
            commandBus.execute(new CreateRegistration(Id.generate(), info));
        }

        server.start();
    }


}
