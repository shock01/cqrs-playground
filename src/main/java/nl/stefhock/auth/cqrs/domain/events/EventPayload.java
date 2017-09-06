package nl.stefhock.auth.cqrs.domain.events;

import java.util.Date;

public interface EventPayload {
    String getAggregateId();

    Date getDate();

    class Builder {
        public String aggregateId;
        public Date date = new Date();

        public Builder aggregateId(String aggregateId) {
            this.aggregateId = aggregateId;
            return this;
        }

        public Builder date(Date date) {
            this.date = date;
            return this;
        }

    }
}
