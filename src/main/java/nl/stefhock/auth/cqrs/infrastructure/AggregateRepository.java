package nl.stefhock.auth.cqrs.infrastructure;

import nl.stefhock.auth.cqrs.domain.aggregates.Aggregate;

/**
 * Created by hocks on 27-12-2016.
 */
public interface AggregateRepository {

    void save(Aggregate aggregate);

    <T extends Aggregate> T byId(String id, Class<T> cls);
}

