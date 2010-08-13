package com.amee.base.resource;

public interface Renderer {

    public void start();

    public void ok();

    public String getMediaType();

    public Object getObject();
}
