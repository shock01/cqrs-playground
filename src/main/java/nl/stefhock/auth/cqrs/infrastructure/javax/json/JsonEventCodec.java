package nl.stefhock.auth.cqrs.infrastructure.javax.json;

import nl.stefhock.auth.cqrs.application.EventCodec;
import nl.stefhock.auth.cqrs.domain.events.DomainEvent;
import nl.stefhock.auth.cqrs.domain.events.Event;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class JsonEventCodec implements EventCodec {

    @Override
    public byte[] encodeDomainEvent(DomainEvent event) {
        final ByteArrayOutputStream bas = new ByteArrayOutputStream();
        final JsonWriter jsonWriter = Json.createWriter(bas);
        jsonWriter.writeObject(event.toJSON());
        jsonWriter.close();
        return bas.toByteArray();
    }

    @Override
    public DomainEvent decodeDomainEvent(byte[] data) {
        final ByteArrayInputStream in = new ByteArrayInputStream(data);
        final JsonReader reader = Json.createReader(in);
        final JsonObject jsonObject = reader.readObject();
        reader.close();

        // convert this please, builder should have a fromJSON method
        jsonObject.getJsonObject("payload");
        Event payload = null; // @todo we need some kinda factory to know which events we have and how to map them, we can add like
        // an annotation to @Event("registrationCreated") or use default className if not present, or @Named
        return new DomainEvent.Builder()
                .fromJson(jsonObject)
                .payload(payload)
                .build();
    }

    @Override
    public Event decodeEvent(byte[] data, Class<Event> type) {
        return null;
    }

    @Override
    public byte[] encodeEvent(Event event) {
        final ByteArrayOutputStream bas = new ByteArrayOutputStream();
        final JsonWriter jsonWriter = Json.createWriter(bas);
        jsonWriter.writeObject(event.toJSON());
        jsonWriter.close();
        return bas.toByteArray();
    }
}
