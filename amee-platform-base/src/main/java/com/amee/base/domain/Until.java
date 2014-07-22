package com.amee.base.domain;

import java.lang.annotation.*;

/**
 * An annotation to mark a Spring bean as being available until a particular API version number.
 *
 * @see Since
 * @see Version
 * @see Versions
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Until {
    public abstract String value() default "x";
}