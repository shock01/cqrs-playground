package nl.stefhock.auth.cqrs.infrastructure.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.stefhock.auth.app.domain.events.PasswordChanged;
import nl.stefhock.auth.app.domain.events.RegistrationCreated;
import nl.stefhock.auth.cqrs.application.EventMapper;
import nl.stefhock.auth.cqrs.domain.events.DomainEvent;
import nl.stefhock.auth.cqrs.domain.events.EventPayload;

import java.io.IOException;

/**
 * Created by hocks on 30-12-2016.
 */
public class JacksonEventMapper implements EventMapper {

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    @Override
    public byte[] toBytes(Object event) {
        try {
            return objectMapper.writeValueAsBytes(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T toEvent(byte[] data, Class<T> type) {
        try {
            return objectMapper.readValue(data, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
