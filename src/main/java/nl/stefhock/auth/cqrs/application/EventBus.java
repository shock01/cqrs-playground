package nl.stefhock.auth.cqrs.application;

/**
 * Created by hocks on 14-7-2017.
 */
public interface EventBus {
    EventBus register(Object object);

    EventBus unregister(Object object);

    void post(Object event);

}
