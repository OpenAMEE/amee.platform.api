package com.amee.restlet.resource;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ValidationResult;
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

import java.util.ArrayList;
import java.util.List;

public class ResourceBuildManager extends ResourceManager {

    public final static DOMOutputter DOM_OUTPUTTER = new DOMOutputter();

    private List<ResourceBuilder> builders = new ArrayList<ResourceBuilder>();

    public void init(GenericResource resource) {
        super.init(resource);
        for (ResourceBuilder builder : getBuilders()) {
            resource.getVariants().add(new Variant(MediaType.valueOf(builder.getMediaType())));
        }
    }

    public Representation getRepresentation(Variant variant) {
        for (ResourceBuilder builder : builders) {
            MediaType mediaType = MediaType.valueOf(builder.getMediaType());
            if (variant.getMediaType().equals(mediaType)) {
                return getRepresentation(builder);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public Representation getRepresentation(ResourceBuilder builder) {
        Representation representation = null;
        MediaType mediaType = MediaType.valueOf(builder.getMediaType());
        if (mediaType.equals(MediaType.APPLICATION_XML)) {
            representation = getDomRepresentation(builder);
        } else if (mediaType.equals(MediaType.APPLICATION_JSON)) {
            representation = getJsonRepresentation(builder);
        }
        return representation;
    }

    protected Representation getJsonRepresentation(ResourceBuilder<JSONObject> builder) {
        Representation representation = null;
        try {
            JSONObject obj = builder.handle(
                    new RequestWrapper(
                            "",
                            getResource().getSupportedVersion(),
                            getAttributes(),
                            getMatrixParameters(),
                            getQueryParameters()));
            // Handle validationResult.
            if ((obj != null) && obj.has("validationResult")) {
                getResource().addValidationResult(new ValidationResult(obj.getJSONObject("validationResult")));
            }
            // Handle status.
            if ((obj != null) && obj.has("status")) {
                if (obj.getString("status").equals("OK")) {
                    representation = new JsonRepresentation(obj);
                } else if (obj.getString("status").equals("INVALID")) {
                    getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                    representation = new JsonRepresentation(obj);
                } else if (obj.getString("status").equals("NOT_FOUND")) {
                    getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
        return representation;
    }

    protected Representation getDomRepresentation(ResourceBuilder<Document> builder) {
        Representation representation = null;
        Document document = builder.handle(
                new RequestWrapper(
                        "",
                        getResource().getSupportedVersion(),
                        getAttributes(),
                        getMatrixParameters(),
                        getQueryParameters()));
        if (document != null) {
            Element representationElement = document.getRootElement();
            if ((representationElement != null) && representationElement.getName().equals("Representation")) {
                // Handle ValidationResult.
                if (representationElement.getChild("ValidationResult") != null) {
                    getResource().addValidationResult(new ValidationResult(representationElement.getChild("ValidationResult")));
                }
                // Handle Status.
                if (representationElement.getChild("Status") != null) {
                    String status = representationElement.getChild("Status").getValue();
                    try {
                        if (status.equals("OK")) {
                            representation = new DomRepresentation(MediaType.APPLICATION_XML, DOM_OUTPUTTER.output(document));
                        } else if (status.equals("INVALID")) {
                            getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                            representation = new DomRepresentation(MediaType.APPLICATION_XML, DOM_OUTPUTTER.output(document));
                        } else if (status.equals("NOT_FOUND")) {
                            getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
                        }
                    } catch (JDOMException e) {
                        throw new RuntimeException("Caught JDOMException: " + e.getMessage(), e);
                    }
                }
            }
        }
        return representation;
    }

    public List<ResourceBuilder> getBuilders() {
        return builders;
    }

    public void setBuilders(List<ResourceBuilder> builders) {
        if (builders != null) {
            this.builders = builders;
        }
    }
}