package com.amee.base.resource;

import org.jdom2.Document;
import org.json.JSONObject;

/**
 * Extends {@link RuntimeException} to provide exception handling for resources. This abstract class
 * provides a basic framework for creating response representations specific to the type of error condition. See
 * the sub-classes for futher detail.
 */
public abstract class ResourceException extends RuntimeException {

    /**
     * Construct a ResourceException without a message.
     */
    public ResourceException() {
        super();
    }

    /**
     * Construct a ResourceException with a message.
     */
    public ResourceException(String message) {
        super(message);
    }

    /**
     * Get the JSON or XML response depending on accepted media type. If the client supports JSON then a JSON
     * representation will be returned, otherwise an XML representation is returned.
     *
     * @param requestWrapper which encapsulates the request
     * @return the response object
     */
    public Object getResponse(RequestWrapper requestWrapper) {
        if (requestWrapper.getAcceptedMediaTypes().contains("application/json")) {
            return getJSONObject();
        } else {
            return getDocument();
        }
    }

    /**
     * Abstract extension point for sub-classes to build a JSON response representation.
     *
     * @return the {@link JSONObject} response representation.
     */
    public abstract JSONObject getJSONObject();

    /**
     * Abstract extension point for sub-classes to build an XML response representation.
     *
     * @return the {@link Document} response representation.
     */
    public abstract Document getDocument();
}
