package com.amee.restlet.resource;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceException;
import com.amee.base.resource.ResourceRemover;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jdom2.Document;
import org.jdom2.Element;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.resource.Variant;

import java.util.HashSet;
import java.util.Set;

public class ResourceRemoveManager extends ResourceManager {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private ResourceRemover remover;
    private Set<MediaType> mediaTypes = new HashSet<MediaType>() {
        {
            add(MediaType.APPLICATION_XML);
            add(MediaType.APPLICATION_JSON);
        }
    };

    @Override
    public void init(GenericResource resource) {
        super.init(resource);
        for (MediaType mediaType : mediaTypes) {
            resource.getVariants().add(new Variant(mediaType));
        }
    }

    public void remove() {
        if (remover != null) {
            Object result;
            RequestWrapper requestWrapper = new RequestWrapper(
                    getResource().getSupportedVersion(),
                    getAcceptedMediaTypes(),
                    getAttributes());
            try {
                result = remover.handle(requestWrapper);
            } catch (ResourceException e) {
                result = e.getResponse(requestWrapper);
            }
            if (result != null) {
                if (JSONObject.class.isAssignableFrom(result.getClass())) {
                    // Deal with JSON
                    if (isOk((JSONObject) result)) {
                        getResponse().setStatus(Status.SUCCESS_OK);
                        getResponse().setEntity(getJsonRepresentation((JSONObject) result));
                    } else if (isNotFound((JSONObject) result)) {
                        getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
                    } else if (isNotAuthenticated((JSONObject) result)) {
                        getResponse().setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
                    } else if (isNotAuthorized((JSONObject) result)) {
                        getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN);
                    } else if (isTimedOut((JSONObject) result)) {
                        getResponse().setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);
                    } else if (isInternalError((JSONObject) result)) {
                        getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
                    } else {
                        getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
                    }
                } else if (Document.class.isAssignableFrom(result.getClass())) {
                    // Deal with DOM
                    Element elm = ((Document) result).getRootElement();
                    if (elm.getChild("Status") != null) {
                        String status = elm.getChild("Status").getValue();
                        if (status.equals("OK")) {
                            getResponse().setStatus(Status.SUCCESS_OK);
                            getResponse().setEntity(getDomRepresentation((Document) result));
                        } else if (status.equals("NOT_FOUND")) {
                            getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
                        } else if (status.equals("NOT_AUTHENTICATED")) {
                            getResponse().setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
                        } else if (status.equals("NOT_AUTHORIZED")) {
                            getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN);
                        } else if (status.equals("TIMED_OUT")) {
                            getResponse().setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);
                        } else if (status.equals("INTERNAL_ERROR")) {
                            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
                        } else {
                            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
                        }
                    } else {
                        getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
                    }
                } else {
                    log.warn("remove() Response media type is not supported.");
                    getResponse().setStatus(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
                }
            } else {
                log.error("getRepresentation() Result was null.");
                getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
            }
        } else {
            throw new UnsupportedOperationException("A ResourceRemover is not available.");
        }
    }

    public ResourceRemover getRemover() {
        return remover;
    }

    public void setRemover(ResourceRemover remover) {
        this.remover = remover;
    }
}
