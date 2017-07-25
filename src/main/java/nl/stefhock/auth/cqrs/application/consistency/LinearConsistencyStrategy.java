package nl.stefhock.auth.cqrs.application.consistency;

import nl.stefhock.auth.cqrs.application.Projection;
import nl.stefhock.auth.cqrs.infrastructure.ProjectionSource;

import java.lang.reflect.Proxy;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by hocks on 17-7-2017.
 * <p>
 * linear consistency will always fetch the latest version from the event store
 * and will synced when the event is behind
 */
public class LinearConsistencyStrategy<T extends Projection<?>> extends ConsistencyStrategy<T> {

    private static final Logger LOGGER = Logger.getLogger(LinearConsistencyStrategy.class.getName());

    public LinearConsistencyStrategy(ConsistencyBuilder.Builder<T> builder) {
        super(builder, queryProxy(builder.registry(), builder.query(), builder.queryProvider()));
    }

    private static <T> T queryProxy(ConsistencyRegistry consistencyRegistry, Class<T> klazz, T query) {
        return (T) Proxy.newProxyInstance(consistencyRegistry.getClass().getClassLoader(),
                new Class[]{klazz}, new ConsistencyRegistryInvocationHandler(consistencyRegistry, query));
    }

    @Override
    public synchronized void synchronize() {

        final ProjectionSource.SequenceInfo info = query().projectionSource().sequenceInfo();
        final long querySequenceId = info.sequenceId();
        final long storeSequenceId = eventStoreSequenceId();
        if (storeSequenceId > querySequenceId) {
            this.suspend();
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info(String.format("[%s] sync:, event:%d, store:%d", this, querySequenceId, storeSequenceId));
            }
            syncBatched(querySequenceId, storeSequenceId);
            this.resume();
        } else {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info(String.format("[%s] synced", this));
            }
        }
    }
}
