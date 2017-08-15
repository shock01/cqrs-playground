package nl.stefhock.auth.cqrs.domain.events;

import java.util.Date;

/**
 * Created by hocks on 19-7-2017.
 */
public abstract class DomainEvent {

    protected String aggregateId;
    protected Date date;

    protected DomainEvent(final String aggregateId, final Date date) {
        this.aggregateId = aggregateId;
        this.date = date;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "AuthEvent{" +
                "withAggregateId='" + aggregateId + '\'' +
                ", withDate=" + date +
                '}';
    }

    public abstract static class Builder<T, E extends DomainEvent> {
        protected final E event;

        protected Builder(E event) {
            this.event = event;
        }

        public T withAggregateId(String aggregateId) {
            event.aggregateId = aggregateId;
            return (T) this;
        }

        public T withDate(Date date) {
            event.date = date;
            return (T) this;
        }

        public E build() {
            return event;
        }
    }
}
