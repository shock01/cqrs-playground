package nl.stefhock.auth.app.infrastructure.projections;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicReference;
import com.hazelcast.core.ISet;
import nl.stefhock.auth.cqrs.infrastructure.ProjectionSource;

import javax.inject.Inject;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by hocks on 25-7-2017.
 */
public class HazelcastProjectionSource<T> extends ProjectionSource<T> {

    private final HazelcastInstance hazelcast;
    private final IAtomicReference<Long> atomicSequenceId;
    private ISet<T> source;

    @Inject
    public HazelcastProjectionSource(final String name, final HazelcastInstance hazelcast) {
        super(name);
        this.hazelcast = hazelcast;
        this.source = hazelcast.getSet(name);
        atomicSequenceId = hazelcast.getAtomicReference(String.format("sequence_%s", name));
    }

    @Override
    public void initialize() {
        Optional.ofNullable(atomicSequenceId.get()).ifPresent(value -> synced(value));
    }

    @Override
    public void tryDelete(T entity) {
        if (source.contains(entity)) {
            source.remove(entity);
        }
    }

    @Override
    public void addOrUpdate(T entity) {
        tryDelete(entity);
        source.add(entity);
    }

    @Override
    public Stream<T> stream() {
        return source.stream().collect(Collectors.toList()).stream();
    }

    @Override
    public void synced(Long sequenceId) {
        atomicSequenceId.set(sequenceId);
        super.synced(sequenceId);
    }

    @Override
    public String toString() {
        return "HazelcastProjectionSource{" +
                "hazelcast=" + hazelcast +
                ", source=" + source +
                "} " + super.toString();
    }
}
