package nl.stefhock.auth.cqrs.infrastructure.javax.json;

import nl.stefhock.auth.cqrs.application.EventCodec;
import nl.stefhock.auth.cqrs.domain.EventFactory;
import nl.stefhock.auth.cqrs.domain.events.DomainEvent;
import nl.stefhock.auth.cqrs.domain.events.Event;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class JsonEventCodec implements EventCodec {

    private final EventFactory eventFactory;

    @Inject
    public JsonEventCodec(EventFactory eventFactory) {
        this.eventFactory = eventFactory;
    }

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

        return new DomainEvent.Builder()
                .fromJson(jsonObject)
                .payload(eventFactory.create(jsonObject.getJsonObject("payload")))
                .build();
    }

    @Override
    public Event decodeEvent(byte[] data, Class<Event> type) {
        final ByteArrayInputStream in = new ByteArrayInputStream(data);
        final JsonReader reader = Json.createReader(in);
        final JsonObject jsonObject = reader.readObject();
        reader.close();
        return eventFactory.create(jsonObject);
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
