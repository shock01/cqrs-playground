package nl.stefhock.auth.cqrs.domain;

import nl.stefhock.auth.cqrs.domain.events.Event;

import javax.json.JsonObject;

public interface EventFactory {
    <T extends Event> T create(JsonObject jsonObject);
}
