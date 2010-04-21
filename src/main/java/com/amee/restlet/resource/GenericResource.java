package com.amee.restlet.resource;

import com.amee.base.domain.Version;
import com.amee.base.resource.ResourceAcceptor;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRemover;
import com.amee.base.resource.ValidationResult;
import com.amee.base.utils.XMLUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.xerces.dom.DocumentImpl;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.DomRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
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
    private List<ValidationResult> validationResults = null;
    private Version since = null;
    private Version until = null;
    private String lastSegment = null;

    public void init(Context context, Request request, Response response) {
        super.init(context, request, response);
        buildManager.init(this);
        acceptManager.init(this);
        removeManager.init(this);
        checkLastSegment();
    }

    /**
     * Checks the lastSegment field matches the last segment of the path.
     */
    public void checkLastSegment() {
        if (!StringUtils.isBlank(getLastSegment())) {
            if (!getRequest().getResourceRef().getLastSegment(false, true).equals(getLastSegment())) {
                getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            }
        }
    }

    /**
     * Handle the GET method.
     *
     * @param variant
     * @return the output representation
     */
    @Override
    public Representation represent(Variant variant) {
        if (!hasValidationResults()) {
            return buildManager.getRepresentation(variant);
        } else {
            return getValidationResultRepresentation(variant.getMediaType());
        }
    }

    protected Representation getValidationResultRepresentation(MediaType mediaType) {
        Representation representation = null;
        if (mediaType.equals(MediaType.APPLICATION_XML)) {
            representation = getValidationResultDomRepresentation();
        } else if (mediaType.equals(MediaType.APPLICATION_JSON)) {
            representation = getValidationResultJsonRepresentation();
        }
        return representation;
    }

    protected Representation getValidationResultJsonRepresentation() {
        try {
            JSONObject result = new JSONObject();
            result.put("status", "INVALID");
            if (!getValidationResults().isEmpty()) {
                if (getValidationResults().size() > 1) {
                    JSONArray validationResults = new JSONArray();
                    result.put("validationResults", validationResults);
                    for (ValidationResult validationResult : getValidationResults()) {
                        validationResults.put(validationResult.getJSONObject());
                    }
                } else {
                    result.put("validationResult", getFirstValidationResult().getJSONObject());
                }
            }
            return new JsonRepresentation(result);
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    protected Representation getValidationResultDomRepresentation() {
        Document document = new DocumentImpl();
        Element representationElem = document.createElement("Representation");
        document.appendChild(representationElem);
        if (!getValidationResults().isEmpty()) {
            if (getValidationResults().size() > 1) {
                representationElem.appendChild(XMLUtils.getElement(document, "Status", "INVALID"));
                Element validationResultsElem = document.createElement("ValidationResults");
                representationElem.appendChild(validationResultsElem);
                for (ValidationResult validationResult : getValidationResults()) {
                    validationResultsElem.appendChild(validationResult.getElement(document));
                }
            } else {
                representationElem.appendChild(getFirstValidationResult().getElement(document));
            }
        }
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

    public Version getSupportedVersion() {
        return (Version) getRequest().getAttributes().get("versionSupported");
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

    public void setAcceptors(Map<String, ResourceAcceptor<Object>> acceptors) {
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

    public boolean hasValidationResults() {
        return (validationResults != null) && !validationResults.isEmpty();
    }

    public List<ValidationResult> getValidationResults() {
        if (validationResults == null) {
            validationResults = new ArrayList<ValidationResult>();
        }
        return validationResults;
    }

    public void setValidationResults(List<ValidationResult> validationResults) {
        getValidationResults().clear();
        if (validationResults != null) {
            getValidationResults().addAll(validationResults);
        }
    }

    public ValidationResult getFirstValidationResult() {
        if (!getValidationResults().isEmpty()) {
            return getValidationResults().get(0);
        } else {
            return null;
        }
    }

    public void addValidationResult(ValidationResult validationResult) {
        if (validationResult != null) {
            getValidationResults().add(validationResult);
        }
    }

    public Version getSince() {
        return since;
    }

    public void setSince(Version since) {
        this.since = since;
    }

    public Version getUntil() {
        return until;
    }

    public void setUntil(Version until) {
        this.until = until;
    }

    public String getLastSegment() {
        return lastSegment;
    }

    public void setLastSegment(String lastSegment) {
        this.lastSegment = lastSegment;
    }
}