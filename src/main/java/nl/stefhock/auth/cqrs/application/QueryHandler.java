package nl.stefhock.auth.cqrs.application;

import nl.stefhock.auth.cqrs.infrastructure.ReadModel;

/**
 * Created by hocks on 24-7-2017.
 */
public abstract class QueryHandler<T> {

    private final ReadModel<T> readModel;

    public QueryHandler(final ReadModel<T> readModel) {
        this.readModel = readModel;
    }

    protected ReadModel<T> readModel() {
        return readModel;
    }
}

