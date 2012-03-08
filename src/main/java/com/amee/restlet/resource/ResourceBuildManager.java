package com.amee.restlet.resource;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceException;
import com.amee.base.resource.ValidationResult;
import com.amee.restlet.MediaTypeUtils;
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
            // Support XML & JSON MediaTypes.
            add(MediaType.APPLICATION_XML);
            add(MediaType.APPLICATION_JSON);

            // Support custom MediaType for EcoSpold.
            add(MediaTypeUtils.APPLICATION_ECOSPOLD_XML);
        }
    };

    @Override
    public void init(GenericResource resource) {
        super.init(resource);
        for (MediaType mediaType : mediaTypes) {
            resource.getVariants().add(new Variant(mediaType));
        }
    }

    /**
     * Get the Representation for the Variant MediaType requested. Returns null if Variant is not supported.
     *
     * @param variant encapsulating MediaType requested
     * @return Representation for the requested Variant
     */
    public Representation getRepresentation(Variant variant) {
        Representation representation = null;
        // We can only supply a Representation if there is ResourceBuilder and the MediaType is supported.
        if ((builder != null) && (mediaTypes.contains(variant.getMediaType()))) {
            // Get the Representation from the ResourceBuilder.
            representation = getRepresentation(builder);
        }
        return representation;
    }

    public Representation getRepresentation(ResourceBuilder builder) {
        Representation representation = null;
        if (builder != null) {
            Object result;
            RequestWrapper requestWrapper = new RequestWrapper(
                    getResource().getSupportedVersion(),
                    getAcceptedMediaTypes(),
                    getAttributes(),
                    getMatrixParameters(),
                    getQueryParameters());
            try {
                result = builder.handle(requestWrapper);
            } catch (ResourceException e) {
                result = e.getResponse(requestWrapper);
            }
            if (result != null) {
                if (JSONObject.class.isAssignableFrom(result.getClass())) {
                    representation = getJsonRepresentation((JSONObject) result);
                } else if (Document.class.isAssignableFrom(result.getClass())) {
                    representation = getDomRepresentation((Document) result);
                } else {
                    log.warn("getRepresentation() Response media type is not supported.");
                    getResponse().setStatus(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
                }
            } else {
                log.error("getRepresentation() Result was null.");
                getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
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