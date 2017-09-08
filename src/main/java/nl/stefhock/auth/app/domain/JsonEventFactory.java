package nl.stefhock.auth.app.domain;

import nl.stefhock.auth.app.domain.events.PasswordChanged;
import nl.stefhock.auth.app.domain.events.RegistrationCreated;
import nl.stefhock.auth.cqrs.domain.EventFactory;
import nl.stefhock.auth.cqrs.domain.events.Event;

import javax.json.JsonObject;

public class JsonEventFactory implements EventFactory {

    @Override
    public <T extends Event> T create(JsonObject jsonObject) {
        final String eventType = jsonObject.getString(Event.EVENT_TYPE);
        switch (eventType) {
            case "registrationcreated":
                return (T) new RegistrationCreated.Builder().fromJson(jsonObject).build();
            case "passwordchanged":
                return (T) new PasswordChanged.Builder().fromJson(jsonObject).build();
            default:
                return null;
        }
    }
}
