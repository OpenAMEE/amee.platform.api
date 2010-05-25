package com.amee.restlet.resource;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ValidationResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.DomRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

import java.util.HashSet;
import java.util.Set;

public class ResourceBuildManager extends ResourceManager {

    private final Log log = LogFactory.getLog(getClass());

    public final static DOMOutputter DOM_OUTPUTTER = new DOMOutputter();

    private ResourceBuilder builder;
    private Set<MediaType> mediaTypes = new HashSet<MediaType>() {
        {
            add(MediaType.APPLICATION_XML);
            add(MediaType.APPLICATION_JSON);
        }
    };

    public void init(GenericResource resource) {
        super.init(resource);
        for (MediaType mediaType : mediaTypes) {
            resource.getVariants().add(new Variant(mediaType));
        }
    }

    public Representation getRepresentation(Variant variant) {
        if ((builder != null) && (mediaTypes.contains(variant.getMediaType()))) {
            return getRepresentation(builder);
        }
        return null;
    }

    public Representation getRepresentation(ResourceBuilder builder) {
        Representation representation = null;
        if (builder != null) {
            Object result = builder.handle(
                    new RequestWrapper(
                            getResource().getSupportedVersion(),
                            getAcceptedMediaTypes(),
                            getAttributes(),
                            getMatrixParameters(),
                            getQueryParameters()));
            if (result != null) {
                if (JSONObject.class.isAssignableFrom(result.getClass())) {
                    representation = getJsonRepresentation((JSONObject) result);
                } else if (Document.class.isAssignableFrom(result.getClass())) {
                    representation = getDomRepresentation((Document) result);
                } else {
                    log.error("getRepresentation() Result MediaType not supported.");
                    getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
                }
            } else {
                log.error("getRepresentation() No result.");
                getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
            }
        }
        return representation;
    }

    protected Representation getJsonRepresentation(JSONObject result) {
        Representation representation = null;
        try {
            // Handle validationResult.
            if ((result != null) && result.has("validationResult")) {
                getResource().addValidationResult(new ValidationResult(result.getJSONObject("validationResult")));
            }
            // Handle status.
            if ((result != null) && result.has("status")) {
                if (isOk(result)) {
                    representation = new JsonRepresentation(result);
                } else if (isInvalid(result)) {
                    getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                    representation = new JsonRepresentation(result);
                } else if (isNotFound(result)) {
                    getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
                } else if (isNotAuthenticated(result)) {
                    getResponse().setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
                } else {
                    log.warn("getJsonRepresentation() Status code not handled: " + result.getString("status"));
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
                // Handle ValidationResult.
                if (result.getChild("ValidationResult") != null) {
                    getResource().addValidationResult(new ValidationResult(result.getChild("ValidationResult")));
                }
                // Handle status.
                if (result.getChild("Status") != null) {
                    String status = result.getChild("Status").getValue();
                    try {
                        if (status.equals("OK")) {
                            representation = new DomRepresentation(MediaType.APPLICATION_XML, DOM_OUTPUTTER.output(document));
                        } else if (status.equals("INVALID")) {
                            getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                            representation = new DomRepresentation(MediaType.APPLICATION_XML, DOM_OUTPUTTER.output(document));
                        } else if (status.equals("NOT_FOUND")) {
                            getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
                        } else if (status.equals("NOT_AUTHENTICATED")) {
                            getResponse().setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
                        } else {
                            log.warn("getDomRepresentation() Status code not handled: " + status);
                        }
                    } catch (JDOMException e) {
                        throw new RuntimeException("Caught JDOMException: " + e.getMessage(), e);
                    }
                }
            }
        }
        return representation;
    }

    public ResourceBuilder getBuilder() {
        return builder;
    }

    public void setBuilder(ResourceBuilder builder) {
        if (builder != null) {
            this.builder = builder;
        }
    }
}