package nl.stefhock.auth.cqrs.domain.events;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.Date;

// @TODO this will break and will cause all problems when new events
// are added by different services
// use @Payload("tralala"), check weld playground for example of getting all types of events
// then register all these payloads inside the EventCodec
// and add a custom serializer maybe in Jackson that will serialize the Payload field
// and is able to create like a map of known events
// this also means that Eventpayload can be in cqrs package
// use aspectJ to handle this
// https://github.com/jponge/guice-aspectj-sample/tree/master/src/main/java/info/ponge/julien/hacks/guiceaspectj
// aspects can also be used to get the sagas and the projections/queries
// or use serviceLoader to get all the classes, which is faster and simpler and does not require aspects
// or use guice multibinder
// http://www.johnchapman.net/technology/coding/guice-java-using-guice-4-0-multibinder-with-provides-and-providesintoset-or-providesintomap/
// http://www.robinhowlett.com/blog/2015/03/19/custom-jackson-polymorphic-deserialization-without-type-metadata/

public abstract class Event {

    protected final String aggregateId;
    protected final Date date;

    protected Event(Builder builder) {
        aggregateId = builder.aggregateId;
        date = builder.date;
    }

    @Override
    public String toString() {
        return "Event{" +
                "aggregateId='" + aggregateId + '\'' +
                ", date=" + date +
                '}';
    }

    public String aggregateId() {
        return aggregateId;
    }

    public Date date() {
        return date;
    }

    public String eventType() {
        return getClass().getSimpleName();
    }

    public abstract JsonObject toJSON();

    protected JsonObjectBuilder jsonBuilder() {
        return Json.createObjectBuilder()
                .add("aggregateId", aggregateId)
                .add("date", date.getTime())
                .add("eventType", eventType());
    }

    protected static class Builder {
        protected String aggregateId;
        protected Date date = new Date();

        public Builder aggregateId(String aggregateId) {
            this.aggregateId = aggregateId;
            return this;
        }

        public Builder date(Date date) {
            this.date = date;
            return this;
        }

        public Builder fromJson(JsonObject jsonObject) {
            aggregateId = jsonObject.getString("aggregateId");
            date = new Date(jsonObject.getInt("date"));
            return this;
        }
    }
}
