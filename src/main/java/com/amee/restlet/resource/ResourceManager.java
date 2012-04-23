package com.amee.restlet.resource;

import com.amee.base.resource.ValidationResult;
import com.amee.restlet.AMEESpringServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.*;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.DomRepresentation;
import org.restlet.resource.Representation;

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
                    attributes.put(attributeName, ((String) a).split(";")[0]);
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

    protected Representation getJsonRepresentation(JSONObject result) {
        Representation representation = null;
        try {
            if (result != null) {
                // Add version.
                result.put("version", getResource().getSupportedVersion().toString());
                // Handle validationResult.
                if (result.has("validationResult")) {
                    getResource().addValidationResult(new ValidationResult(result.getJSONObject("validationResult")));
                }
                // Handle status.
                if (result.has("status")) {
                    if (isOk(result)) {
                        representation = new JsonRepresentation(result);
                    } else if (isInvalid(result)) {
                        getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                        representation = new JsonRepresentation(result);
                    } else if (isNotFound(result)) {
                        getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
                    } else if (isNotAuthenticated(result)) {
                        getResponse().setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
                    } else if (isNotAuthorized(result)) {
                        getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN);
                    } else if (isTimedOut(result)) {
                        getResponse().setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);
                    } else if (isMediaTypeNotSupported(result)) {
                        getResponse().setStatus(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
                    } else if (isInternalError(result)) {
                        getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
                    } else {
                        log.warn("getJsonRepresentation() Status code not handled: " + result.getString("status"));
                    }
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
        return representation;
    }

    protected Representation getDomRepresentation(Document document) {
        Representation representation = null;
        if (document != null) {
            Element result = document.getRootElement();
            if ((result != null) && result.getName().equals("Representation")) {
                // Add version.
                result.addContent(new Element("Version").setText(getResource().getSupportedVersion().toString()));
                // Handle ValidationResult.
                if (result.getChild("ValidationResult") != null) {
                    getResource().addValidationResult(new ValidationResult(result.getChild("ValidationResult")));
                }
                // Handle status.
                if (result.getChild("Status") != null) {
                    String status = result.getChild("Status").getValue();
                    try {
                        if (status.equals("OK")) {
                            representation = new DomRepresentation(MediaType.APPLICATION_XML, ResourceBuildManager.DOM_OUTPUTTER.output(document));
                        } else if (status.equals("INVALID")) {
                            getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                            representation = new DomRepresentation(MediaType.APPLICATION_XML, ResourceBuildManager.DOM_OUTPUTTER.output(document));
                        } else if (status.equals("NOT_FOUND")) {
                            getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
                        } else if (status.equals("NOT_AUTHENTICATED")) {
                            getResponse().setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
                        } else if (status.equals("NOT_AUTHORIZED")) {
                            getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN);
                        } else if (status.equals("TIMED_OUT")) {
                            getResponse().setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);
                        } else if (status.equals("MEDIA_TYPE_NOT_SUPPORTED")) {
                            getResponse().setStatus(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
                        } else if (status.equals("INTERNAL_ERROR")) {
                            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
                        } else {
                            log.warn("getDomRepresentation() Status code not handled: " + status);
                        }
                    } catch (JDOMException e) {
                        throw new RuntimeException("Caught JDOMException: " + e.getMessage(), e);
                    }
                }
            } else if ((result != null) && result.getName().equals("ecoSpold")) {
                try {
                    representation = new DomRepresentation(MediaType.valueOf("application/x.ecospold+xml"), ResourceBuildManager.DOM_OUTPUTTER.output(document));
                } catch (JDOMException e) {
                    throw new RuntimeException("Caught JDOMException: " + e.getMessage(), e);
                }
            }
        }
        return representation;
    }
}
