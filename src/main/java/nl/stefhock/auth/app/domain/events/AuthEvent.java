package nl.stefhock.auth.app.domain.events;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Date;

/**
 * Created by hocks on 16-8-2017.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class AuthEvent {

    private final String aggregateId;
    private final Date date;

    public AuthEvent(Builder builder) {
        this.aggregateId = builder.aggregateId;
        this.date = builder.date;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public Date getDate() {
        return date;
    }


    abstract static class Builder {
        String aggregateId;
        Date date = new Date();

        Builder aggregateId(String aggregateId) {
            this.aggregateId = aggregateId;
            return this;
        }

    }
}
