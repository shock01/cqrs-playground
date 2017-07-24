/**
 * Created by hocks on 4-7-2017.
 */
package nl.stefhock.auth.app;

import com.google.inject.Guice;
import com.google.inject.Injector;
import nl.stefhock.auth.app.application.ApplicationModule;
import nl.stefhock.auth.app.application.command.RegistrationCommand;
import nl.stefhock.auth.app.domain.DomainModule;
import nl.stefhock.auth.app.infrastructure.InfrastructureModule;
import nl.stefhock.auth.cqrs.application.CommandBus;
import nl.stefhock.auth.cqrs.application.consistency.ConsistencyRegistry;
import nl.stefhock.auth.cqrs.domain.Id;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

import static nl.stefhock.auth.app.application.JerseyResourceConfig.newResourceConfig;

public class Main {


    public static void main(String[] args) throws Exception {

        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        final Injector injector = Guice.createInjector(
                new ApplicationModule(),
                new DomainModule(),
                new InfrastructureModule());
        final ResourceConfig resourceConfig = newResourceConfig(injector);
        final URI baseUri = UriBuilder.fromUri("http://localhost").port(9000).build();
        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, resourceConfig, false);
        final StaticHttpHandler staticHttpHandler = new StaticHttpHandler("ui");
        server.getServerConfiguration().addHttpHandler(staticHttpHandler, "/ui");

        // dumb insert
        // @TODO do not always run this!! its just for development purposes now
        injector.getInstance(DbMigration.class).migrate();
        int i = 10;
        ConsistencyRegistry registry = injector.getInstance(ConsistencyRegistry.class);
        registry.pauseAll();
        final CommandBus commandBus = injector.getInstance(CommandBus.class);
        while (i-- > 0) {
            final RegistrationCommand.RegistrationInfo info = new RegistrationCommand.RegistrationInfo(String.format("%d@greetz.com", i), String.format("%d@greetz.com", i), null);
            commandBus.execute(new RegistrationCommand(Id.generate(), info));
        }
        // @todo we should sync all the BaseProjection on start up maybe..
        // or have a policy if it should (Strategy)
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
        server.start();
        Thread.currentThread().join();
    }


}
