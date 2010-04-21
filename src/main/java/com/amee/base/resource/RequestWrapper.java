package com.amee.base.resource;

import com.amee.base.domain.Version;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class RequestWrapper implements Serializable {

    private Version version;
    private Map<String, String> attributes = new HashMap<String, String>();
    private Map<String, String> matrixParameters = new HashMap<String, String>();
    private Map<String, String> queryParameters = new HashMap<String, String>();
    private Map<String, String> formParameters = new HashMap<String, String>();
    private byte[] body = null;
    private String mediaType = "";

    public RequestWrapper() {
        super();
    }

    public RequestWrapper(
            Version version,
            Map<String, String> attributes) {
        this();
        setVersion(version);
        setAttributes(attributes);
    }

    public RequestWrapper(
            Version version,
            Map<String, String> attributes,
            Map<String, String> matrixParameters,
            Map<String, String> queryParameters) {
        this(version, attributes);
        setMatrixParameters(matrixParameters);
        setQueryParameters(queryParameters);
    }

    public RequestWrapper(
            Version version,
            Map<String, String> attributes,
            Map<String, String> matrixParameters,
            Map<String, String> queryParameters,
            Map<String, String> formParameters) {
        this(version, attributes, matrixParameters, queryParameters);
        setFormParameters(formParameters);
        setMediaType("application/x-www-form-urlencoded");
    }

    public RequestWrapper(
            Version version,
            Map<String, String> attributes,
            Map<String, String> matrixParameters,
            Map<String, String> queryParameters,
            InputStream body,
            String mediaType) {
        this(version, attributes, matrixParameters, queryParameters);
        setBody(body);
        setMediaType(mediaType);
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        if (attributes != null) {
            this.attributes = attributes;
        }
    }

    public Map<String, String> getMatrixParameters() {
        return matrixParameters;
    }

    public void setMatrixParameters(Map<String, String> matrixParameters) {
        if (matrixParameters != null) {
            this.matrixParameters = matrixParameters;
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

    public boolean hasBody() {
        return body != null;
    }

    public byte[] getBody() {
        return body;
    }

    public String getBodyAsString() {
        try {
            return new String(body, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Caught UnsupportedEncodingException: " + e.getMessage(), e);
        }
    }

    public void setBody(InputStream stream) {
        try {
            body = IOUtils.toByteArray(stream);
        } catch (IOException e) {
            throw new RuntimeException("Caught IOException: " + e.getMessage(), e);
        }
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