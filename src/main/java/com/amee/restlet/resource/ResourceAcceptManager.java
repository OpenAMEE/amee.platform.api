package com.amee.restlet.resource;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceAcceptor;
import com.amee.base.resource.ResourceException;
import com.amee.base.resource.ValidationResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.resource.Representation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ResourceAcceptManager extends ResourceManager {

    private final Log log = LogFactory.getLog(getClass());

    private Map<String, ResourceAcceptor<Object>> acceptors = new HashMap<String, ResourceAcceptor<Object>>();

    public void accept(Representation entity) {
        // A POST or PUT must have an entity content body.
        if (entity.isAvailable()) {
            // Lookup correct ResourceAcceptor, based on media-type.
            MediaType mediaType = entity.getMediaType();
            if (acceptors.containsKey(mediaType.getName())) {
                // Send RequestWrapper to ResourceAcceptor.
                Object result = null;
                RequestWrapper requestWrapper = getRequestWrapper(entity);
                try {
                    result = acceptors.get(mediaType.getName()).handle(requestWrapper);
                } catch (ResourceException e) {
                    result = e.getResponse(requestWrapper);
                }
                if (result != null) {
                    // Handle the various result media types.
                    if (JSONObject.class.isAssignableFrom(result.getClass())) {
                        handle((JSONObject) result);
                    } else {
                        log.warn("accept() Response media type is not supported.");
                        getResponse().setStatus(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
                    }
                } else {
                    log.error("accept() Result was null.");
                    getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
                }
            } else {
                getResponse().setStatus(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
            }
        } else {
            getResponse().setStatus(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
        }
    }

    public void handle(JSONObject result) {
        if (isOk(result)) {
            if (getRequest().getMethod().equals(Method.POST)) {
                getResponse().setStatus(Status.SUCCESS_CREATED);
            } else if (getRequest().getMethod().equals(Method.PUT)) {
                getResponse().setStatus(Status.SUCCESS_CREATED);
            } else {
                getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
            }
        } else if (isInvalid(result)) {
            try {
                if (result.has("validationResult")) {
                    getResource().addValidationResult(new ValidationResult(result.getJSONObject("validationResult")));
                    getResource().handleGet();
                } else if (result.has("validationResults")) {
                    JSONArray arr = result.getJSONArray("validationResults");
                    for (int i = 0; i < arr.length(); i++) {
                        getResource().addValidationResult(new ValidationResult(arr.getJSONObject(i)));
                    }
                    getResource().handleGet();
                }
            } catch (JSONException e) {
                throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
            }
            getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
        } else if (isNotFound(result)) {
            getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
        } else if (isNotAuthenticated(result)) {
            getResponse().setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
        } else if (isTimedOut(result)) {
            getResponse().setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);
        } else {
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
        }
    }

    /**
     * Create and return RequestWrapper for either standard form parameters or streamed media-type.
     *
     * @param entity to create RequestWrapper from
     * @return a new RequestWrapper
     */
    protected RequestWrapper getRequestWrapper(Representation entity) {
        if (entity.getMediaType().equals(MediaType.APPLICATION_WWW_FORM)) {
            return new RequestWrapper(
                    getResource().getSupportedVersion(),
                    getAcceptedMediaTypes(),
                    getAttributes(),
                    getMatrixParameters(),
                    getQueryParameters(),
                    new Form(entity).getValuesMap());
        } else {
            try {
                return new RequestWrapper(
                        getResource().getSupportedVersion(),
                        getAcceptedMediaTypes(),
                        getAttributes(),
                        getMatrixParameters(),
                        getQueryParameters(),
                        entity.getStream(),
                        entity.getMediaType().getName());
            } catch (IOException e) {
                throw new RuntimeException("Caught IOException: " + e.getMessage());
            }
        }
    }

    public Map<String, ResourceAcceptor<Object>> getAcceptors() {
        return acceptors;
    }

    public void setAcceptors(Map<String, ResourceAcceptor<Object>> acceptors) {
        if (acceptors != null) {
            this.acceptors = acceptors;
        }
    }
}