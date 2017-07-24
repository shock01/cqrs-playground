package nl.stefhock.auth.cqrs.application.consistency;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by hocks on 23-7-2017.
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface Sync {
}
