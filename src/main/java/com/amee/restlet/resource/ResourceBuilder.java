package com.amee.restlet.resource;

public interface ResourceBuilder<E> {

    public E build(RequestWrapper requestWrapper);

    public String getMediaType();
}
