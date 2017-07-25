package nl.stefhock.auth.app.infrastructure.projections;

import nl.stefhock.auth.cqrs.infrastructure.ProjectionSource;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Created by hocks on 23-7-2017.
 */
public class MemoryProjectionSource<T> extends ProjectionSource<T> {
    @Override
    public String toString() {
        return "MemoryProjectionSource{" +
                "source=" + source +
                "} " + super.toString();
    }

    private final Set<T> source;

    public MemoryProjectionSource(String name) {
        super(name);
        source = new HashSet<>();
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
        return source.stream();
    }
}
