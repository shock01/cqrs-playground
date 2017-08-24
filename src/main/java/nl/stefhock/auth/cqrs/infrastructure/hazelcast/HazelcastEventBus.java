package nl.stefhock.auth.cqrs.infrastructure.hazelcast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import nl.stefhock.auth.cqrs.application.EventBus;
import nl.stefhock.auth.cqrs.domain.events.DomainEvent;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by hocks on 22-8-2017.
 */
public class HazelcastEventBus implements EventBus, MessageListener<String> {

    private static final Logger LOGGER = Logger.getLogger(HazelcastEventBus.class.getName());
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
    }

    private final EventBus eventBus;
    private final ITopic<String> topic;

    HazelcastEventBus(EventBus eventBus, ITopic<String> topic) {
        this.eventBus = eventBus;
        this.topic = topic;
    }

    public static EventBus factory(HazelcastInstance hazelcastInstance, EventBus eventBus) {
        final HazelcastEventBus instance = new HazelcastEventBus(eventBus, hazelcastInstance.getTopic("events"));
        instance.listenForEvents();
        return instance;
    }

    private void listenForEvents() {
        topic.addMessageListener(this);
    }

    @Override
    public EventBus register(Object object) {
        eventBus.register(object);
        return this;
    }

    @Override
    public EventBus unregister(Object object) {
        eventBus.unregister(this);
        return this;
    }

    @Override
    public void post(DomainEvent event) {
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "post event", event);
        }
        try {
            topic.publish(objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onMessage(Message<String> message) {
        // java serializable will not work with non java code..
        // what about protocol buffers ? nodejs ?? ObjectMapper ???
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, String.format("receiving event message: %s", message.getMessageObject()));
        }
        try {
            final DomainEvent event = objectMapper.readValue(message.getMessageObject(), DomainEvent.class);
            eventBus.post(event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
