package com.amee.base.resource;

/**
 * Implementations of this interface are expected to handle a 'remover' request, embodied in
 * a {@link RequestWrapper}, and return a representation object.
 * <p/>
 * A 'remover' request is typically an HTTP DELETE request.
 *
 * @param <E> the type of output representation object
 */
public interface ResourceRemover<E> extends ResourceHandler<E> {
}
