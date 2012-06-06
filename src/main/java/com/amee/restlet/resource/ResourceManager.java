package com.amee.restlet.resource;

import com.amee.base.resource.ValidationResult;
import com.amee.restlet.AMEESpringServer;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Preference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.DomRepresentation;
import org.restlet.resource.Representation;

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
                        // URLDecoder decodes application/x-www-form-urlencoded Strings, which should only appear in the body of a POST.
                        // It decodes "+" symbols to spaces, which breaks ISO time formats that include a "+", so we manually encode them
                        // here and immediately decode them again in order to preserve them.
                        value = URLDecoder.decode(value.replace("+", "%2B"), "UTF-8").replace("%2B", "+");
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
        // Get query string
        org.restlet.data.Reference ref = getRequest().getResourceRef();
        String query = ref.getQuery(false);

        if (query != null) {
            /*
             *  The query parameters could be retrieved by calling:
             * 
             *  getRequest().getResourceRef().getQueryAsForm().getValuesMap();
             * 
             *  The problem with that is that the Reference.getQueryAsForm() method calls a Form constructor which decodes the query string with
             *  URLDecoder.decode, which is appropriate only for application/x-www-form-urlencoded strings in POST bodies.  It decodes "+" symbols
             *  to spaces, which breaks ISO time formats that include a "+", so we manually encode them here before passing them to the Form
             *  constructor, and immediately decode them again in order to preserve them.  Note that we make an effort to only encode "+" symbols
             *  that are part of a date string, in order to avoid breaking other query parameters that may have been submitted with "+" symbols in
             *  place of spaces (some clients do this - plus and space characters are fairly interchangeable).  The regex used just looks for a
             *  sequence of 6 digits preceeding the plus symbol which may or may not be broken up by colons, and a sequence of two digits following
             *  it; the assumption is that a timezone offset will not be submitted unless a time is also submitted.
             *
             *  TODO: Check this is still needed after upgrading Restlet to 2.0.
             *  It may be possible to use the java.net.URI class to properly encode this.
             *  Also see: http://stackoverflow.com/questions/724043/http-url-address-encoding-in-java.
             */
            // Encode + symbols
            org.restlet.data.Form form = new org.restlet.data.Form(query.replaceAll("(\\d\\d:?\\d\\d:?\\d\\d)\\+(\\d\\d)", "$1%2B$2"));
            Map<String, String> params = form.getValuesMap();

            // Decode + symbols again
            for (String param : params.keySet()) {
                params.put(param, params.get(param).replace("%2B", "+"));
            }
            return params;
        } else {
            return new HashMap<String, String>();
        }
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
