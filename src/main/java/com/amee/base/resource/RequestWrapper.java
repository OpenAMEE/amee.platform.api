package com.amee.base.resource;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RequestWrapper implements Serializable {

    private Map<String, String> attributes = new HashMap<String, String>();
    private Map<String, String> queryParameters = new HashMap<String, String>();
    private Map<String, String> formParameters = new HashMap<String, String>();
    private InputStream stream = null;
    private String mediaType = "";

    public RequestWrapper() {
        super();
    }

    public RequestWrapper(
            Map<String, String> attributes) {
        this();
        setAttributes(attributes);
    }

    public RequestWrapper(
            Map<String, String> attributes,
            Map<String, String> queryParameters) {
        this(attributes);
        setQueryParameters(queryParameters);
    }

    public RequestWrapper(
            Map<String, String> attributes,
            Map<String, String> queryParameters,
            Map<String, String> formParameters) {
        this(attributes, queryParameters);
        setFormParameters(formParameters);
        setMediaType("application/x-www-form-urlencoded");
    }

    public RequestWrapper(
            Map<String, String> attributes,
            Map<String, String> queryParameters,
            InputStream stream,
            String mediaType) {
        this(attributes, queryParameters);
        setStream(stream);
        setMediaType(mediaType);
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        if (attributes != null) {
            this.attributes = attributes;
        }
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    public void setQueryParameters(Map<String, String> queryParameters) {
        if (queryParameters != null) {
            this.queryParameters = queryParameters;
        }
    }

    public Map<String, String> getFormParameters() {
        return formParameters;
    }

    public void setFormParameters(Map<String, String> formParameters) {
        if (formParameters != null) {
            this.formParameters = formParameters;
        }
    }

    public InputStream getStream() {
        return stream;
    }

    public void setStream(InputStream stream) {
        this.stream = stream;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        if (mediaType != null) {
            this.mediaType = mediaType;
        }
    }
}