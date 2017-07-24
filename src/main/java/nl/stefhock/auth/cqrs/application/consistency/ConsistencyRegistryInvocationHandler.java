package nl.stefhock.auth.cqrs.application.consistency;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Created by hocks on 17-7-2017.
 */
public class ConsistencyRegistryInvocationHandler implements InvocationHandler {
    private final Object impl;
    private final ConsistencyRegistry consistencyRegistry;

    public ConsistencyRegistryInvocationHandler(ConsistencyRegistry consistencyRegistry, Object impl) {
        this.consistencyRegistry = consistencyRegistry;
        this.impl = impl;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (Optional.ofNullable(method.getAnnotation(Sync.class)).isPresent()) {
            consistencyRegistry.locate(this.impl).ifPresent(value -> value.synchronize());
        }
        return method.invoke(this.impl, args);
    }
}
