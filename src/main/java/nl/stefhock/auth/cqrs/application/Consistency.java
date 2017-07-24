package nl.stefhock.auth.cqrs.application;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by hocks on 14-7-2017.
 */
@Target(value = {ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Consistency {
    Policy policy() default Policy.LINEAR;

    public enum Policy {
        LINEAR
    }

}
