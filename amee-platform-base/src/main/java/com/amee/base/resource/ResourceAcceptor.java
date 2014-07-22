package com.amee.base.resource;

/**
 * Implementations of this interface are expected to handle an 'accept' request, embodied in
 * a {@link RequestWrapper}, and return a representation object.
 * <p/>
 * An 'accept' request is typically an HTTP POST or PUT request, with incoming form parameters or a body.
 *
 * @param <E> the type of output representation object
 */
public interface ResourceAcceptor<E> extends ResourceHandler<E> {
}
