package nl.stefhock.auth.cqrs.domain;


import java.util.List;

/**
 * Created by hocks on 29-12-2016.
 * <p>
 * this is jvm specific and will not run on all JVMS
 *
 * @see {http://www.javaspecialists.eu/archive/Issue175.html}
 */
public class AggregateFactory {

    private static <T> T create(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot create aggregate", e);
        }
    }

    public <T extends Aggregate> T create(Class<T> cls, List<DomainEvent> events) {
        return replayEvents(events, cls);
    }

    private <T extends Aggregate> T replayEvents(List<DomainEvent> events, Class<T> cls) {
        final T t;
        try {
            t = create(cls);
            events.stream().forEach(event -> t.apply(event));
        } catch (Exception e) {
            throw new RuntimeException("cannot instantiate aggregate", e);
        }
        return t;
    }

}
