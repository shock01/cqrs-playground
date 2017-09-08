package nl.stefhock.auth.cqrs.infrastructure.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import nl.stefhock.auth.cqrs.application.EventBus;
import nl.stefhock.auth.cqrs.application.EventCodec;
import nl.stefhock.auth.cqrs.domain.events.DomainEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by hocks on 22-8-2017.
 */
public class HazelcastEventBus implements EventBus, MessageListener<String> {

    private static final Logger LOGGER = Logger.getLogger(HazelcastEventBus.class.getName());
    private final EventBus eventBus;
    private final EventCodec eventCodec;
    private final ITopic<String> topic;

    HazelcastEventBus(EventBus eventBus, EventCodec eventCodec, ITopic<String> topic) {
        this.eventBus = eventBus;
        this.eventCodec = eventCodec;
        this.topic = topic;
    }

    public static EventBus factory(HazelcastInstance hazelcastInstance, EventBus eventBus, EventCodec eventCodec) {
        final HazelcastEventBus instance = new HazelcastEventBus(eventBus, eventCodec, hazelcastInstance.getTopic("events"));
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
        final String message = new String(eventCodec.encodeDomainEvent(event));
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, String.format("message for topic: %s", message));
        }
        topic.publish(message);

    }

    @Override
    public void onMessage(Message<String> message) {
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, String.format("receiving event message: %s", message.getMessageObject()));
        }
        final DomainEvent event = eventCodec.decodeDomainEvent(message.getMessageObject().getBytes());
        eventBus.post(event);
    }
}
