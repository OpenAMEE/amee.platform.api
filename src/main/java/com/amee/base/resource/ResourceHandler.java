package com.amee.base.resource;

/**
 * Implementations of this interface are expected to handle a request, embodied in a {@link RequestWrapper}, and
 * return a representation object.
 * <p/>
 * Direct implementations are not expected. Instead it is expected that one of the sub-interfaces will be
 * implemented. These are {@link ResourceAcceptor}, {@link ResourceBuilder} and {@link ResourceRemover}.
 *
 * @param <E> the type of output representation object
 */
public interface ResourceHandler<E> {

    /**
     * Handle a request, embodied in a {@link RequestWrapper}, and return a representation object.
     *
     * @param requestWrapper RequestWrapper for this request
     * @return the output representation object
     */
    public E handle(RequestWrapper requestWrapper);
}
