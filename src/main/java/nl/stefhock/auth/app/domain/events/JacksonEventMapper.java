package nl.stefhock.auth.app.domain.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import nl.stefhock.auth.cqrs.domain.DomainEvent;
import nl.stefhock.auth.cqrs.domain.EventMapper;

import java.io.IOException;

/**
 * Created by hocks on 30-12-2016.
 */
public class JacksonEventMapper implements EventMapper {

    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper(new SmileFactory());
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
    public <T extends DomainEvent> T toEvent(byte[] data, Class<?> cls) {
        try {
            return objectMapper.readValue(data, (Class<T>) cls);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
