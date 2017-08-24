package nl.stefhock.auth.cqrs.domain.events;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * Created by hocks on 19-7-2017.
 *
 * @FIXME its bad that domainEvent is serializable however domainEvent
 * will get payload and then its resolved
 * <p>
 * should be DomainEvent<T> where T is in this concept AuthEvent...or any event
 */
@JsonDeserialize(builder = DomainEvent.Builder.class)
public final class DomainEvent {

    private final String type;
    private final String aggregateId;
    private final Long sequence;
    private final long timestamp;
    private Object payload;

    private DomainEvent(Builder builder) {
        aggregateId = builder.aggregateId;
        timestamp = builder.timestamp;
        sequence = builder.sequence;
        payload = builder.payload;
        type = builder.type;
    }

    @SuppressWarnings("unused")
    public String getAggregateId() {
        return aggregateId;
    }

    @SuppressWarnings("unused")
    public long getTimestamp() {
        return timestamp;
    }


    @SuppressWarnings("unused")
    public Long getSequence() {
        return sequence;
    }

    public Object getPayload() {
        return payload;
    }

    @SuppressWarnings("unused")
    public String getType() {
        return type;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        private String aggregateId;
        private long timestamp;
        private Long sequence;
        private Object payload;
        private String type;

        public Builder aggregateId(String aggregateId) {
            this.aggregateId = aggregateId;
            return this;
        }

        public Builder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder sequence(long sequence) {
            this.sequence = sequence;
            return this;
        }

        public Builder payload(Object payload) {
            this.payload = payload;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public DomainEvent build() {
            return new DomainEvent(this);
        }

    }


}
