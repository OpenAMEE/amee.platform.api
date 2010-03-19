package com.amee.base.resource;

public interface ResourceAcceptor<E> {

    public E accept(RequestWrapper requestWrapper);
}
