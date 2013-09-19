package com.amee.base.resource;

import com.amee.base.domain.Version;
import com.amee.base.validation.ValidationException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Encapsulates properties and behaviour for requests intended for a resource. Typically the request will
 * originate in a HTTP call and the terminology here reflects that. It's also possible that a RequestWrapper
 * may represent a request from a non-HTTP source.
 */
public class RequestWrapper implements Serializable {

    /**
     * The name of the Spring resource bean needed to handle the request.
     */
    private String target = "";

    /**
     * A {@link Version} indicating the API version desired by the request.
     */
    private Version version;

    /**
     * A {@link List} of media-type names.
     */
    private List<String> acceptedMediaTypes = new ArrayList<String>();

    /**
     * A {@link Map} of request attributes as String key-value pairs.
     */
    private Map<String, String> attributes = new HashMap<String, String>();

    /**
     * A {@link Map} of request matrix parameters as String key-value pairs.
     */
    private Map<String, String> matrixParameters = new HashMap<String, String>();

    /**
     * A {@link Map} of request query parameters as String key-value pairs.
     */
    private Map<String, String> queryParameters = new HashMap<String, String>();

    /**
     * A {@link Map} of request form parameters as String key-value pairs.
     */
    private Map<String, String> formParameters = new HashMap<String, String>();

    /**
     * A byte array forming a request body. This is typically an HTTP POST or PUT body.
     */
    private byte[] body = null;

    /**
     * The media-type of the request body.
     */
    private String mediaType = "";

    /**
     * Construct a empty RequestWrapper.
     */
    public RequestWrapper() {
        super();
    }

    /**
     * Construct a RequestWrapper with specific properties.
     *
     * @param version            the API {@link Version}
     * @param acceptedMediaTypes the accepted media-types
     * @param attributes         the request attributes
     */
    public RequestWrapper(
            Version version,
            List<String> acceptedMediaTypes,
            Map<String, String> attributes) {
        this();
        setVersion(version);
        setAcceptedMediaTypes(acceptedMediaTypes);
        setAttributes(attributes);
    }

    /**
     * Construct a RequestWrapper with specific properties.
     *
     * @param version            the API {@link Version}
     * @param acceptedMediaTypes the accepted media-types
     * @param attributes         the request attributes
     * @param matrixParameters   the request matrix parameters
     * @param queryParameters    the request query parameters
     */
    public RequestWrapper(
            Version version,
            List<String> acceptedMediaTypes,
            Map<String, String> attributes,
            Map<String, String> matrixParameters,
            Map<String, String> queryParameters) {
        this(version, acceptedMediaTypes, attributes);
        setMatrixParameters(matrixParameters);
        setQueryParameters(queryParameters);
    }

    /**
     * Construct a RequestWrapper with specific properties.
     *
     * @param version            the API {@link Version}
     * @param acceptedMediaTypes the accepted media-types
     * @param attributes         the request attributes
     * @param matrixParameters   the request matrix parameters
     * @param queryParameters    the request query parameters
     * @param formParameters     the request form parameters
     */
    public RequestWrapper(
            Version version,
            List<String> acceptedMediaTypes,
            Map<String, String> attributes,
            Map<String, String> matrixParameters,
            Map<String, String> queryParameters,
            Map<String, String> formParameters) {
        this(version, acceptedMediaTypes, attributes, matrixParameters, queryParameters);
        setFormParameters(formParameters);
        setMediaType("application/x-www-form-urlencoded");
    }

    /**
     * Construct a RequestWrapper with specific properties.
     *
     * @param version            the API {@link Version}
     * @param acceptedMediaTypes the accepted media-types
     * @param attributes         the request attributes
     * @param matrixParameters   the request matrix parameters
     * @param queryParameters    the request query parameters
     * @param body               the request body
     * @param mediaType          the request body media-type
     */
    public RequestWrapper(
            Version version,
            List<String> acceptedMediaTypes,
            Map<String, String> attributes,
            Map<String, String> matrixParameters,
            Map<String, String> queryParameters,
            InputStream body,
            String mediaType) {
        this(version, acceptedMediaTypes, attributes, matrixParameters, queryParameters);
        setBody(body);
        setMediaType(mediaType);
    }

    /**
     * Construct a RequestWrapper from a {link JSONObject}.
     *
     * @param obj a {@link JSONObject}
     */
    public RequestWrapper(JSONObject obj) {
        super();
        try {
            setTarget(obj.getString("target"));
            setVersion(new Version(obj.getString("version")));
            addToList(getAcceptedMediaTypes(), obj, "acceptedMediaTypes");
            addToMap(getAttributes(), obj, "attributes");
            addToMap(getMatrixParameters(), obj, "matrixParameters");
            addToMap(getQueryParameters(), obj, "queryParameters");
            addToMap(getFormParameters(), obj, "formParameters");
            setMediaType(obj.getString("mediaType"));
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    /**
     * Serialize the RequestWrapper to a {@link JSONObject}.
     *
     * @return {@link JSONObject} representing the RequestWrapper
     */
    public JSONObject toJSONObject() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("target", getTarget());
            obj.put("version", getVersion().toString());
            obj.put("acceptedMediaTypes", new JSONArray(getAcceptedMediaTypes()));
            obj.put("attributes", new JSONObject(getAttributes()));
            obj.put("matrixParameters", new JSONObject(getMatrixParameters()));
            obj.put("queryParameters", new JSONObject(getQueryParameters()));
            obj.put("formParameters", new JSONObject(getFormParameters()));
            obj.put("mediaType", getMediaType());
            return obj;
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    private static void addToMap(Map<String, String> m, JSONObject obj, String name) throws JSONException {
        JSONObject node = obj.getJSONObject(name);
        for (Iterator i = node.keys(); i.hasNext();) {
            String key = (String) i.next();
            if (!node.isNull(key)) {
                m.put(key, node.getString(key));
            } else {
                m.put(key, "");
            }
        }
    }

    private static void addToList(List<String> s, JSONObject obj, String name) throws JSONException {
        JSONArray arr = obj.getJSONArray(name);
        for (int i = 0; i < arr.length(); i++) {
            s.add((String) arr.get(i));
        }
    }

    private static void setMapFromMap(Map<String, String> target, Map<String, String> source) {
        target.clear();
        if (source != null) {
            for (Map.Entry<String, String> entry : source.entrySet()) {
                target.put(entry.getKey(), entry.getValue() != null ? entry.getValue() : "");
            }
        }
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public List<String> getAcceptedMediaTypes() {
        return acceptedMediaTypes;
    }

    public void setAcceptedMediaTypes(List<String> acceptedMediaTypes) {
        this.acceptedMediaTypes.clear();
        if (acceptedMediaTypes != null) {
            this.acceptedMediaTypes.addAll(acceptedMediaTypes);
        }
    }

    /**
     * Gets request attributes as String key-value pairs.
     *
     * @return a Map of request attributes.
     */
    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        setMapFromMap(this.attributes, attributes);
    }

    /**
     * Gets request matrix parameters as String key-value pairs.
     * As our matrix parameters are single values, the Map's values will be the empty String.
     * eg: country => ""
     *
     * @return a Map of request matrix parameters.
     */
    public Map<String, String> getMatrixParameters() {
        return matrixParameters;
    }

    public void setMatrixParameters(Map<String, String> matrixParameters) {
        setMapFromMap(this.matrixParameters, matrixParameters);
    }

    /**
     * Gets request query parameters as String key-value pairs.
     *
     * @return a Map of request query parameters.
     */
    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    public void setQueryParameters(Map<String, String> queryParameters) {
        setMapFromMap(this.queryParameters, queryParameters);
    }

    /**
     * Gets request form (POST body) parameters as String key-value pairs.
     *
     * @return a Map of request form parameters.
     */
    public Map<String, String> getFormParameters() {
        return formParameters;
    }

    public void setFormParameters(Map<String, String> formParameters) {
        setMapFromMap(this.formParameters, formParameters);
    }

    /**
     * Gets all submitted query and form parameters as String key-value pairs.
     *
     * @return a Map of all submitted query and form parameters.
     */
    public Map<String, String> getAllParameters() {
        Map<String, String> allParameters = new HashMap<String, String>();
        allParameters.putAll(getQueryParameters());
        allParameters.putAll(getFormParameters());
        return allParameters;
    }

    public boolean hasBody() {
        return body != null;
    }

    /**
     * Gets the request body as a byte array. This is typically the submitted POST or PUT body.
     * You probably want to use on of the getBodyAs* methods, eg: {@code getBodyAsString}
     *
     * @return a byte array containing the submitted body.
     */
    public byte[] getBody() {
        return body;
    }

    /**
     * Gets the request body byte array as a UTF-8 string.
     *
     * @return the request body byte array as a string
     */
    public String getBodyAsString() {
        try {
            return new String(body, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Caught UnsupportedEncodingException: " + e.getMessage(), e);
        }
    }

    /**
     * Returns the request body as a Document.
     *
     * @return document
     */
    public Document getBodyAsDocument() {
        try {
            if (hasBody()) {
                return new SAXBuilder().build(new ByteArrayInputStream(getBody()));
            } else {
                throw new RuntimeException("Cannot create a Document when there is an empty body.");
            }
        } catch (JDOMException e) {
            throw new ValidationException(e.getMessage());
        } catch (IOException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    /**
     * Returns the request body as a JSONObject.
     *
     * @return JSONObject
     */
    public JSONObject getBodyAsJSONObject() {
        try {
            if (hasBody()) {
                return new JSONObject(getBodyAsString());
            } else {
                throw new RuntimeException("Cannot create a JSONObject when there is an empty body.");
            }
        } catch (JSONException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    /**
     * Set the request body byte array from the contents of an {@link InputStream}.
     *
     * @param stream to set body from
     */
    public void setBody(InputStream stream) {
        try {
            body = IOUtils.toByteArray(stream);
        } catch (IOException e) {
            throw new RuntimeException("Caught IOException: " + e.getMessage());
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

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        if (target != null) {
            this.target = target;
        }
    }
}