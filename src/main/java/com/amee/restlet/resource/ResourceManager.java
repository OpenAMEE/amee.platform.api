package com.amee.restlet.resource;

import com.amee.restlet.AMEESpringServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Preference;
import org.restlet.data.Request;
import org.restlet.data.Response;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

public class ResourceManager {

    private final Log log = LogFactory.getLog(getClass());

    private GenericResource resource;

    public void init(GenericResource resource) {
        this.resource = resource;
    }

    /**
     * This is how we tell if the request came via HTTPS as SSL is terminated at the load balancer.
     *
     * @return true if the current request has come through the secure connector
     */
    protected boolean isSecure() {
        return getActiveServer().isSecure();
    }

    protected boolean isOk(JSONObject result) {
        return isStatus(result, "OK");
    }

    protected boolean isNotFound(JSONObject result) {
        return isStatus(result, "NOT_FOUND");
    }

    protected boolean isNotAuthenticated(JSONObject result) {
        return isStatus(result, "NOT_AUTHENTICATED");
    }

    protected boolean isNotAuthorized(JSONObject result) {
        return isStatus(result, "NOT_AUTHORIZED");
    }

    protected boolean isInternalError(JSONObject result) {
        return isStatus(result, "INTERNAL_ERROR");
    }

    protected boolean isInvalid(JSONObject result) {
        return isStatus(result, "INVALID");
    }

    protected boolean isTimedOut(JSONObject result) {
        return isStatus(result, "TIMED_OUT");
    }

    protected boolean isMediaTypeNotSupported(JSONObject result) {
        return isStatus(result, "MEDIA_TYPE_NOT_SUPPORTED");
    }

    protected boolean isStatus(JSONObject result, String status) {
        try {
            return (result != null) && result.has("status") && result.getString("status").equals(status);
        } catch (JSONException e) {
            // Swallow.
            return false;
        }
    }

    protected Map<String, String> getAttributes() {
        Map<String, String> attributes = new HashMap<String, String>();
        for (String attributeName : getAttributeNames()) {
            if (getRequest().getAttributes().containsKey(attributeName)) {
                Object a = getRequest().getAttributes().get(attributeName);
                if (a instanceof String) {
                    // This removes any matrix parameters.
                    String value = ((String) a).split(";")[0];
                    try {
                        value = URLDecoder.decode(value, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        log.warn("getAttributes() Caught UnsupportedEncodingException: " + e.getMessage());
                    }
                    attributes.put(attributeName, value);
                } else {
                    log.warn("getAttributes() Attribute value is not a String: " + attributeName);
                }
            } else {
                log.warn("getAttributes() Attribute value not found: " + attributeName);
            }
        }
        return attributes;
    }

    protected Map<String, String> getMatrixParameters() {
        return getRequest().getResourceRef().getMatrixAsForm().getValuesMap();
    }

    protected Map<String, String> getQueryParameters() {
        return getRequest().getResourceRef().getQueryAsForm().getValuesMap();
    }

    protected List<String> getAcceptedMediaTypes() {
        List<String> acceptedMediaTypes = new ArrayList<String>();
        for (Preference<MediaType> p : getRequest().getClientInfo().getAcceptedMediaTypes()) {
            acceptedMediaTypes.add(p.getMetadata().toString());
        }
        return acceptedMediaTypes;
    }

    public GenericResource getResource() {
        return resource;
    }

    public Request getRequest() {
        return resource.getRequest();
    }

    public Response getResponse() {
        return resource.getResponse();
    }

    public Set<String> getAttributeNames() {
        return resource.getAttributeNames();
    }

    public void setAttributeNames(Set<String> attributeNames) {
        resource.setAttributeNames(attributeNames);
    }

    public AMEESpringServer getActiveServer() {
        return (AMEESpringServer) getRequest().getAttributes().get("activeServer");
    }
}
