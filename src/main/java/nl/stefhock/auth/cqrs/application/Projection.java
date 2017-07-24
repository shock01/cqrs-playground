package nl.stefhock.auth.cqrs.application;

import nl.stefhock.auth.cqrs.infrastructure.ProjectionSource;

/**
 * Created by hocks on 24-7-2017.
 */
public interface Projection<T> {
    ProjectionSource<T> projectionSource();
}
