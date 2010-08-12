package com.amee.base.resource;

public interface Renderer {

    public void start(RequestWrapper requestWrapper);

    public void ok();

    public String getMediaType();

    public Object getObject();
}
