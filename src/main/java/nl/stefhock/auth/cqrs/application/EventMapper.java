package nl.stefhock.auth.cqrs.application;


/**
 * Created by hocks on 20-7-2017.
 */
public interface EventMapper {
    byte[] toBytes(Object event);

    <T> T toEvent(byte[] data, Class<T> type);

}
