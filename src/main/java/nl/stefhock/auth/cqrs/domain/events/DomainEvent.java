package nl.stefhock.auth.cqrs.domain.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Created by hocks on 19-7-2017.
 */
public final class DomainEvent {

    private final String type;
    private final String aggregateId;
    private final long sequence;
    private final long timestamp;
    private Event payload;

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
    public long getSequence() {
        return sequence;
    }

    public Event getPayload() {
        return payload;
    }

    @SuppressWarnings("unused")
    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "DomainEvent{" +
                "eventType='" + type + '\'' +
                ", aggregateId='" + aggregateId + '\'' +
                ", sequence=" + sequence +
                ", timestamp=" + timestamp +
                ", payload=" + payload +
                '}';
    }

    @JsonIgnore
    public JsonObject toJSON() {
        return Json.createObjectBuilder()
                .add("aggregateId", aggregateId)
                .add("eventType", type)
                .add("sequence", sequence)
                .add("timestamp", timestamp)
                .add("payload", payload.toJSON())
                .build();
    }

    public static class Builder {
        private String aggregateId;
        private long timestamp;
        private long sequence;
        private Event payload;
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

        public Builder payload(Event payload) {
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

        public Builder fromJson(JsonObject jsonObject) {
            aggregateId = jsonObject.getString("aggregateId");
            type = jsonObject.getString("eventType");
            sequence = jsonObject.getInt("sequence");
            timestamp = jsonObject.getInt("timestamp");
            return this;
        }
    }
}
