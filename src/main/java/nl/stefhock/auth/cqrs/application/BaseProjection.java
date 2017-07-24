package nl.stefhock.auth.cqrs.application;

import nl.stefhock.auth.cqrs.infrastructure.ProjectionSource;

/**
 * Created by hocks on 24-7-2017.
 */
public abstract class BaseProjection<T> implements Projection<T> {

    private final ProjectionSource<T> projectionSource;

    public BaseProjection(final ProjectionSource<T> projectionSource) {
        this.projectionSource = projectionSource;
    }

    public ProjectionSource<T> projectionSource() {
        return projectionSource;
    }
}

