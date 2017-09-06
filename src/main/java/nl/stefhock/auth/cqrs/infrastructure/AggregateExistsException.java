package nl.stefhock.auth.cqrs.infrastructure;

import nl.stefhock.auth.cqrs.domain.events.EventPayload;

/**
 * Created by hocks on 27-12-2016.
 */
public class AggregateExistsException extends RuntimeException {
    private final String aggregateId;
    private final EventPayload source;

    public AggregateExistsException(String aggregateId, EventPayload source) {
        super(String.format("Aggregate already exists: %s", aggregateId));
        this.aggregateId = aggregateId;
        this.source = source;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public EventPayload source() {
        return source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AggregateExistsException that = (AggregateExistsException) o;

        if (!aggregateId.equals(that.aggregateId)) return false;
        return source.equals(that.source);
    }

    @Override
    public int hashCode() {
        int result = aggregateId.hashCode();
        result = 31 * result + source.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AggregateExistsException{" +
                "aggregateId='" + aggregateId + '\'' +
                ", source=" + source +
                "} " + super.toString();
    }
}
