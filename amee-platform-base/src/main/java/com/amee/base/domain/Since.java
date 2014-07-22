package com.amee.base.domain;

import java.lang.annotation.*;

/**
 * An annotation to mark a Spring bean as being available since a particular API version number.
 *
 * @See Until
 * @see Version
 * @see Versions
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Since {
    String value() default "0";
}
