package com.amee.restlet.resource;

import com.amee.base.domain.Version;
import com.amee.base.resource.ResourceAcceptor;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRemover;
import com.amee.base.resource.ValidationResult;
import com.amee.restlet.MediaTypeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Context;
import org.restlet.data.*;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.*;

import java.util.*;

public class GenericResource extends Resource {

    private final Log log = LogFactory.getLog(getClass());

    public final static DOMOutputter DOM_OUTPUTTER = new DOMOutputter();

    private ResourceBuildManager buildManager = null;
    private ResourceAcceptManager acceptManager = null;
    private ResourceRemoveManager removeManager = null;
    private Boolean allowPost = null;
    private Boolean allowPut = null;
    private Boolean allowDelete = null;
    private List<ValidationResult> validationResults = null;
    private Version since = null;
    private Version until = null;
    private String lastSegment = null;
    private Set<String> attributeNames = new HashSet<String>();

    public void init(Context context, Request request, Response response) {
        super.init(context, request, response);
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
        Representation representation;
        // Get a Representation.
        if (!hasValidationResults()) {
            representation = getBuildManager().getRepresentation(variant);
        } else {
            representation = getValidationResultRepresentation(variant.getMediaType());
        }
        // Update Representation with CharacterSet.
        if (representation != null) {
            representation.setCharacterSet(CharacterSet.UTF_8);
        } else {
            log.warn("represent() Representation was null.");
        }
        return representation;
    }

    protected Representation getValidationResultRepresentation(MediaType mediaType) {
        Representation representation = null;
        if (MediaTypeUtils.isXML(mediaType)) {
            representation = getValidationResultDomRepresentation();
        } else if (MediaTypeUtils.isJSON(mediaType)) {
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
        Document document = new Document();
        Element representationElem = new Element("Representation");
        document.setRootElement(representationElem);
        if (!getValidationResults().isEmpty()) {
            if (getValidationResults().size() > 1) {
                representationElem.addContent(new Element("Status").setText("INVALID"));
                Element validationResultsElem = new Element("ValidationResults");
                representationElem.addContent(validationResultsElem);
                for (ValidationResult validationResult : getValidationResults()) {
                    validationResultsElem.addContent(validationResult.getElement());
                }
            } else {
                representationElem.addContent(getFirstValidationResult().getElement());
            }
        }
        try {
            return new DomRepresentation(MediaType.APPLICATION_XML, DOM_OUTPUTTER.output(document));
        } catch (JDOMException e) {
            throw new RuntimeException("Caught JDOMException: " + e.getMessage(), e);
        }
    }

    /**
     * Handle the POST method.
     *
     * @param entity that has been POSTed
     * @throws ResourceException
     */
    public void acceptRepresentation(Representation entity)
            throws ResourceException {
        getAcceptManager().accept(entity);
    }

    /**
     * Handle the PUT method.
     *
     * @param entity that has been PUT
     * @throws ResourceException
     */
    public void storeRepresentation(Representation entity)
            throws ResourceException {
        getAcceptManager().accept(entity);
    }

    /**
     * Handle the DELETE method.
     *
     * @throws ResourceException
     */
    public void removeRepresentations() throws ResourceException {
        getRemoveManager().remove();
    }

    public Version getSupportedVersion() {
        return (Version) getRequest().getAttributes().get("versionSupported");
    }

    public Set<String> getAttributeNames() {
        return attributeNames;
    }

    public void setAttributeNames(Set<String> attributeNames) {
        if (attributeNames != null) {
            this.attributeNames = attributeNames;
        }
    }

    public ResourceBuildManager getBuildManager() {
        if (buildManager == null) {
            buildManager = new ResourceBuildManager();
            buildManager.init(this);
        }
        return buildManager;
    }

    public void setBuildManager(ResourceBuildManager buildManager) {
        if (buildManager != null) {
            this.buildManager = buildManager;
        }
    }

    public void setBuilder(ResourceBuilder builder) {
        getBuildManager().setBuilder(builder);
    }

    public ResourceAcceptManager getAcceptManager() {
        if (acceptManager == null) {
            acceptManager = new ResourceAcceptManager();
            acceptManager.init(this);
        }
        return acceptManager;
    }

    public void setAcceptManager(ResourceAcceptManager acceptManager) {
        if (acceptManager != null) {
            this.acceptManager = acceptManager;
        }
    }

    public void setAcceptors(Map<String, ResourceAcceptor<Object>> acceptors) {
        getAcceptManager().setAcceptors(acceptors);
    }

    public ResourceRemoveManager getRemoveManager() {
        if (removeManager == null) {
            removeManager = new ResourceRemoveManager();
            removeManager.init(this);
        }
        return removeManager;
    }

    public void setRemoveManager(ResourceRemoveManager removeManager) {
        if (removeManager != null) {
            this.removeManager = removeManager;
        }
    }

    public void setRemover(ResourceRemover remover) {
        getRemoveManager().setRemover(remover);
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