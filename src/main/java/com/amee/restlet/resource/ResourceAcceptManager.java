package com.amee.restlet.resource;

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

    private Map<String, ResourceAcceptor> acceptors = new HashMap<String, ResourceAcceptor>();

    public void accept(Representation entity) {
        // A POST or PUT must have an entity content body.
        if (entity.isAvailable()) {
            // Lookup correct RequestWrapper, based on media-type.
            MediaType mediaType = entity.getMediaType();
            if (acceptors.containsKey(mediaType.getName())) {
                // Send RequestWrapper to ResourceAcceptor.
                JSONObject result = acceptors.get(mediaType.getName()).accept(getRequestWrapper(entity));
                if (isOk(result)) {
                    if (getRequest().getMethod().equals(Method.POST)) {
                        getResponse().setStatus(Status.SUCCESS_CREATED);
                    } else if (getRequest().getMethod().equals(Method.PUT)) {
                        getResponse().setStatus(Status.SUCCESS_NO_CONTENT);
                    } else {
                        getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
                    }
                } else if (isInvalid(result)) {
                    // TODO: Add feedback to response.
                    getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                } else if (isNotFound(result)) {
                    getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
                } else {
                    getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
                }
            } else {
                getResponse().setStatus(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
            }
        } else {
            getResponse().setStatus(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
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
                    getAttributes(),
                    getQueryParameters(),
                    new Form(entity).getValuesMap());
        } else {
            try {
                return new RequestWrapper(
                        getAttributes(),
                        getQueryParameters(),
                        entity.getStream(),
                        entity.getMediaType().getName());
            } catch (IOException e) {
                throw new RuntimeException("Caught IOException: " + e.getMessage(), e);
            }
        }
    }

    public Map<String, ResourceAcceptor> getAcceptors() {
        return acceptors;
    }

    public void setAcceptors(Map<String, ResourceAcceptor> acceptors) {
        if (acceptors != null) {
            this.acceptors = acceptors;
        }
    }
}