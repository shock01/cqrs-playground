package nl.stefhock.auth.app.application;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.Injector;
import nl.stefhock.auth.app.api.HealthResource;
import nl.stefhock.auth.app.api.projections.RegistrationResources;
import nl.stefhock.auth.app.api.StatisticsResource;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.ServiceLocatorProvider;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

import javax.annotation.Priority;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.TimeZone;

/**
 * Created by hocks on 12-7-2017.
 */
public class JerseyResourceConfig extends ResourceConfig {
    JerseyResourceConfig(Injector injector) {
        register(LoggingFeature.class);
        register(new GuiceFeature(injector));
        register(ExceptionMapperProvider.class);
        register(ObjectMapperProvider.class);
        // resources
        register(HealthResource.class);
        register(RegistrationResources.class);
        register(StatisticsResource.class);
    }

    public static ResourceConfig newResourceConfig(Injector injector) {
        return new JerseyResourceConfig(injector);
    }

    @Priority(1)
    public static class GuiceFeature implements Feature {

        private final Injector injector;

        public GuiceFeature(final Injector injector) {
            this.injector = injector;
        }

        @Override
        public boolean configure(FeatureContext context) {
            final ServiceLocator serviceLocator = ServiceLocatorProvider.getServiceLocator(context);
            GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
            final GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
            guiceBridge.bridgeGuiceInjector(injector);
            return true;
        }
    }

    @Provider
    public static class ObjectMapperProvider implements ContextResolver<ObjectMapper> {

        @Override
        public ObjectMapper getContext(Class<?> arg0) {
            return new ObjectMapper()
                    .disableDefaultTyping()
                    .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                    .setTimeZone(TimeZone.getTimeZone("UTC"));
        }
    }

    @Provider
    public static class ExceptionMapperProvider implements ExceptionMapper<Throwable> {
        @Override
        public Response toResponse(Throwable e) {
            return Response.serverError().entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
        }
    }
}
