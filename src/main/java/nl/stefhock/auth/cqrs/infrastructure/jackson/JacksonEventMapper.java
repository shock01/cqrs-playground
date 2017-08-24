package nl.stefhock.auth.cqrs.infrastructure.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.stefhock.auth.cqrs.application.EventMapper;

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

    private final Class<?> baseEvent;

    public JacksonEventMapper(final Class<?> baseEvent) {
        this.baseEvent = baseEvent;
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
    public <T> T toEvent(byte[] data) {
        try {
            return objectMapper.readValue(data, (Class<T>) baseEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
