package nl.stefhock.auth.cqrs.domain.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import nl.stefhock.auth.app.domain.events.PasswordChanged;
import nl.stefhock.auth.app.domain.events.RegistrationCreated;

import java.util.Date;
// @TODO this will break and will cause all problems when new events
// are added by different services
// use @Payload("tralala"), check weld playground for example of getting all types of events
// then register all these payloads inside the EventMapper
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
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "eventType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = RegistrationCreated.class),
        @JsonSubTypes.Type(value = PasswordChanged.class)
})
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
