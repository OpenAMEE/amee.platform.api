package com.amee.restlet.resource;

import com.amee.base.resource.Feedback;
import com.amee.base.resource.ResourceAcceptor;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRemover;
import com.amee.base.utils.XMLUtils;
import org.apache.xerces.dom.DocumentImpl;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.DomRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class GenericResource extends Resource {

    private ResourceBuildManager buildManager = new ResourceBuildManager();
    private ResourceAcceptManager acceptManager = new ResourceAcceptManager();
    private ResourceRemoveManager removeManager = new ResourceRemoveManager();
    private Boolean allowPost = null;
    private Boolean allowPut = null;
    private Boolean allowDelete = null;
    private Feedback feedback = null;

    public void init(Context context, Request request, Response response) {
        super.init(context, request, response);
        buildManager.init(this);
        acceptManager.init(this);
        removeManager.init(this);
    }

    /**
     * Handle the GET method.
     *
     * @param variant
     * @return the output representation
     */
    @Override
    public Representation represent(Variant variant) {
        if (feedback == null) {
            return buildManager.getRepresentation(variant);
        } else {
            return getFeedbackRepresentation(variant.getMediaType());
        }
    }

    protected Representation getFeedbackRepresentation(MediaType mediaType) {
        Representation representation = null;
        if (mediaType.equals(MediaType.APPLICATION_XML)) {
            representation = getFeedbackDomRepresentation();
        } else if (mediaType.equals(MediaType.APPLICATION_JSON)) {
            representation = getFeedbackJsonRepresentation();
        }
        return representation;
    }

    protected Representation getFeedbackJsonRepresentation() {
        try {
            JSONObject result = new JSONObject();
            result.put("status", "INVALID");
            result.put("feedback", feedback.getJSONObject());
            return new JsonRepresentation(result);
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    protected Representation getFeedbackDomRepresentation() {
        Document document = new DocumentImpl();
        Element representationElem = document.createElement("Representation");
        document.appendChild(representationElem);
        representationElem.appendChild(XMLUtils.getElement(document, "Status", "INVALID"));
        representationElem.appendChild(feedback.getElement(document));
        return new DomRepresentation(MediaType.APPLICATION_XML, document);
    }

    /**
     * Handle the POST method.
     *
     * @param entity that has been POSTed
     * @throws ResourceException
     */
    public void acceptRepresentation(Representation entity)
            throws ResourceException {
        acceptManager.accept(entity);
    }

    /**
     * Handle the PUT method.
     *
     * @param entity that has been PUT
     * @throws ResourceException
     */
    public void storeRepresentation(Representation entity)
            throws ResourceException {
        acceptManager.accept(entity);
    }

    /**
     * Handle the DELETE method.
     *
     * @throws ResourceException
     */
    public void removeRepresentations() throws ResourceException {
        removeManager.remove();
    }

    public void setAttributeNames(Set<String> attributeNames) {
        buildManager.setAttributeNames(attributeNames);
        acceptManager.setAttributeNames(attributeNames);
        removeManager.setAttributeNames(attributeNames);
    }

    public ResourceBuildManager getBuildManager() {
        return buildManager;
    }

    public void setBuildManager(ResourceBuildManager buildManager) {
        if (buildManager != null) {
            this.buildManager = buildManager;
        }
    }

    public void setBuilders(List<ResourceBuilder> builders) {
        buildManager.setBuilders(builders);
    }

    public ResourceAcceptManager getAcceptManager() {
        return acceptManager;
    }

    public void setAcceptManager(ResourceAcceptManager acceptManager) {
        if (acceptManager != null) {
            this.acceptManager = acceptManager;
        }
    }

    public void setAcceptors(Map<String, ResourceAcceptor<JSONObject>> acceptors) {
        acceptManager.setAcceptors(acceptors);
    }

    public ResourceRemoveManager getRemoveManager() {
        return removeManager;
    }

    public void setRemoveManager(ResourceRemoveManager removeManager) {
        if (removeManager != null) {
            this.removeManager = removeManager;
        }
    }

    public void setRemover(ResourceRemover<JSONObject> remover) {
        removeManager.setRemover(remover);
    }

    public boolean allowPost() {
        return allowPost != null ? allowPost : super.allowPost();
    }

    public void setAllowPost(Boolean allowPost) {
        this.allowPost = allowPost;
    }

    public boolean allowPut() {
        return allowPut != null ? allowPut : super.allowPut();
    }

    public void setAllowPut(Boolean allowPut) {
        this.allowPut = allowPut;
    }

    public boolean allowDelete() {
        return allowDelete != null ? allowDelete : super.allowDelete();
    }

    public void setAllowDelete(Boolean allowDelete) {
        this.allowDelete = allowDelete;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }
}