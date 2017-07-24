package nl.stefhock.auth.app.infrastructure.projections;

import nl.stefhock.auth.cqrs.infrastructure.ProjectionSource;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Created by hocks on 23-7-2017.
 */
public class MemoryProjectionSource<T> extends ProjectionSource<T> {

    private final Set<T> set;

    public MemoryProjectionSource() {
        super();
        set = new HashSet<>();
    }

    @Override
    public void tryDelete(T entity) {
        if (set.contains(entity)) {
            set.remove(entity);
        }
    }

    @Override
    public void addOrUpdate(T entity) {
        tryDelete(entity);
        set.add(entity);
    }

    @Override
    public Stream<T> stream() {
        return set.stream();
    }
}
