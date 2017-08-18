package nl.stefhock.auth.app.application;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * Created by hocks on 18-8-2017.
 */
public class Server {

    private final ResourceConfig resourceConfig;
    private final int port;

    public Server(final int port, final ResourceConfig resourceConfig) {
        this.port = port;
        this.resourceConfig = resourceConfig;
    }

    public void start() throws Exception {
        final URI baseUri = UriBuilder.fromUri("http://localhost").port(port).build();
        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, resourceConfig, false);
        final StaticHttpHandler staticHttpHandler = new StaticHttpHandler("ui");
        staticHttpHandler.setFileCacheEnabled(false);
        server.getServerConfiguration().addHttpHandler(staticHttpHandler, "/ui");
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
        server.start();
        Thread.currentThread().join();
    }
}
