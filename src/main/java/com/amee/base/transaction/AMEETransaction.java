package com.amee.base.transaction;

import java.lang.annotation.*;

/**
 * An annotation to mark the start and end of an AMEE 'transaction'. This annotation is
 * detected by the {@link AMEETransactionAspect} aspect to publish {@link TransactionEvent}s before and after
 * invocations of the annotated method.
 * <p/>
 * This annotation must not be nested in method stacks.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AMEETransaction {
}
