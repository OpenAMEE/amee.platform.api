package com.amee.restlet.resource;

import org.json.JSONObject;

public interface ResourceAcceptor {

    public JSONObject accept(RequestWrapper requestWrapper);
}
