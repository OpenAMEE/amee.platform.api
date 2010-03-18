package com.amee.restlet.resource;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Resource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ResourceManager {

    private Resource resource;
    private Set<String> attributeNames = new HashSet<String>();

    public void init(Resource resource) {
        this.resource = resource;
    }

    protected boolean isOk(JSONObject result) {
        try {
            return (result != null) && result.has("status") && result.getString("status").equals("OK");
        } catch (JSONException e) {
            // Swallow.
            return false;
        }
    }

    protected boolean isNotFound(JSONObject result) {
        try {
            return (result != null) && result.has("status") && result.getString("status").equals("NOT_FOUND");
        } catch (JSONException e) {
            // Swallow.
            return false;
        }
    }

    protected boolean isInvalid(JSONObject result) {
        try {
            return (result != null) && result.has("status") && result.getString("status").equals("INVALID");
        } catch (JSONException e) {
            // Swallow.
            return false;
        }
    }

    protected Map<String, String> getAttributes() {
        Map<String, String> attributes = new HashMap<String, String>();
        for (String attributeName : attributeNames) {
            if (getRequest().getAttributes().containsKey(attributeName)) {
                // TODO: Type safety.
                attributes.put(attributeName, (String) getRequest().getAttributes().get(attributeName));
            } else {
                // TODO: Do something?
            }
        }
        return attributes;

    }

    protected Map<String, String> getQueryParameters() {
        return getRequest().getResourceRef().getQueryAsForm().getValuesMap();
    }

    public Resource getResource() {
        return resource;
    }

    public Request getRequest() {
        return resource.getRequest();
    }

    public Response getResponse() {
        return resource.getResponse();
    }

    public Set<String> getAttributeNames() {
        return attributeNames;
    }

    public void setAttributeNames(Set<String> attributeNames) {
        if (attributeNames != null) {
            this.attributeNames = attributeNames;
        }
    }
}
