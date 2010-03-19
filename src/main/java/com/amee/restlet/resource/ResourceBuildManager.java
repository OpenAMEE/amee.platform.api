package com.amee.restlet.resource;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.DomRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.Variant;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class ResourceBuildManager extends ResourceManager {

    private List<ResourceBuilder> builders = new ArrayList<ResourceBuilder>();

    public void init(Resource resource) {
        super.init(resource);
        for (ResourceBuilder builder : getBuilders()) {
            resource.getVariants().add(new Variant(MediaType.valueOf(builder.getMediaType())));
        }
    }

    public Representation getRepresentation(Variant variant) {
        Representation representation = null;
        for (ResourceBuilder builder : builders) {
            MediaType mediaType = MediaType.valueOf(builder.getMediaType());
            if (variant.getMediaType().equals(mediaType)) {
                representation = getRepresentation(builder);
            }
        }
        return representation;
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
            JSONObject obj = builder.build(new RequestWrapper(getAttributes(), getQueryParameters()));
            if ((obj != null) && obj.has("status")) {
                if (obj.getString("status").equals("OK")) {
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
        Document document = builder.build(new RequestWrapper(getAttributes(), getQueryParameters()));
        if (document != null) {
            Node representationNode = document.getFirstChild();
            if ((representationNode != null) && representationNode.getNodeName().equals("Representation")) {
                NodeList childNodes = representationNode.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    if (childNodes.item(i).getNodeName().equals("Status")) {
                        Node statusNode = childNodes.item(i);
                        String status = statusNode.getTextContent();
                        if (status.equals("OK")) {
                            representation = new DomRepresentation(MediaType.APPLICATION_XML, document);
                        } else if (status.equals("NOT_FOUND")) {
                            getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
                        }
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