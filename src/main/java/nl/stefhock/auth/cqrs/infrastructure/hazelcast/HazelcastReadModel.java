package nl.stefhock.auth.cqrs.infrastructure.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicReference;
import com.hazelcast.core.ISet;
import nl.stefhock.auth.cqrs.infrastructure.ReadModel;

import javax.inject.Inject;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by hocks on 25-7-2017.
 */
public class HazelcastReadModel<T> extends ReadModel<T> {

    private final IAtomicReference<Long> atomicSequenceId;
    private final ISet<T> source;

    // @FIXME MOVE TO A FACTORY METHOD
    private HazelcastReadModel(final String name, final ISet<T> set, final IAtomicReference<Long> sequenceId) {
        super(name);
        source = set;
        atomicSequenceId = sequenceId;
    }

    public static <T> HazelcastReadModel<T> factory(String name, final HazelcastInstance hazelcast) {
        final ISet<T> set = hazelcast.getSet(name);
        final IAtomicReference<Long> sequenceId = hazelcast.getAtomicReference(String.format("sequence_%s", name));
        final HazelcastReadModel instance = new HazelcastReadModel(name, set, sequenceId);
        instance.initialize();
        return instance;
    }

    private void initialize() {
        Optional.ofNullable(atomicSequenceId.get()).ifPresent(value -> synced(value));
    }

    @Override
    public void reset() {
        source.clear();
        atomicSequenceId.set(0L);
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
        return "HazelcastReadModel{" +
                "source=" + source +
                "} " + super.toString();
    }
}
