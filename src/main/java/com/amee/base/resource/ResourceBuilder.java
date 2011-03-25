package com.amee.base.resource;

/**
 * Implementations of this interface are expected to handle a 'builder' request, embodied in
 * a {@link RequestWrapper}, and return a representation object.
 * <p/>
 * An 'builder' request is typically an HTTP GET request.
 *
 * @param <E> the type of output representation object
 */
public interface ResourceBuilder<E> extends ResourceHandler<E> {
}
