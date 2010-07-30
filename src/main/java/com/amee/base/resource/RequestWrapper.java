package com.amee.base.resource;

import com.amee.base.domain.Version;
import com.amee.base.validation.ValidationException;
import org.apache.commons.io.IOUtils;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

public class RequestWrapper implements Serializable {

    private String target = "";
    private Version version;
    private Set<String> acceptedMediaTypes = new HashSet<String>();
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
            Set<String> acceptedMediaTypes,
            Map<String, String> attributes) {
        this();
        setVersion(version);
        setAcceptedMediaTypes(acceptedMediaTypes);
        setAttributes(attributes);
    }

    public RequestWrapper(
            Version version,
            Set<String> acceptedMediaTypes,
            Map<String, String> attributes,
            Map<String, String> matrixParameters,
            Map<String, String> queryParameters) {
        this(version, acceptedMediaTypes, attributes);
        setMatrixParameters(matrixParameters);
        setQueryParameters(queryParameters);
    }

    public RequestWrapper(
            Version version,
            Set<String> acceptedMediaTypes,
            Map<String, String> attributes,
            Map<String, String> matrixParameters,
            Map<String, String> queryParameters,
            Map<String, String> formParameters) {
        this(version, acceptedMediaTypes, attributes, matrixParameters, queryParameters);
        setFormParameters(formParameters);
        setMediaType("application/x-www-form-urlencoded");
    }

    public RequestWrapper(
            Version version,
            Set<String> acceptedMediaTypes,
            Map<String, String> attributes,
            Map<String, String> matrixParameters,
            Map<String, String> queryParameters,
            InputStream body,
            String mediaType) {
        this(version, acceptedMediaTypes, attributes, matrixParameters, queryParameters);
        setBody(body);
        setMediaType(mediaType);
    }

    public RequestWrapper(JSONObject obj) {
        super();
        try {
            setTarget(obj.getString("target"));
            setVersion(new Version(obj.getString("version")));
            addToSet(getAcceptedMediaTypes(), obj, "acceptedMediaTypes");
            addToMap(getAttributes(), obj, "attributes");
            addToMap(getMatrixParameters(), obj, "matrixParameters");
            addToMap(getQueryParameters(), obj, "queryParameters");
            addToMap(getFormParameters(), obj, "formParameters");
            setMediaType(obj.getString("mediaType"));
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

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

    protected void addToMap(Map<String, String> m, JSONObject obj, String name) throws JSONException {
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

    protected void addToSet(Set<String> s, JSONObject obj, String name) throws JSONException {
        JSONArray arr = obj.getJSONArray(name);
        for (int i = 0; i < arr.length(); i++) {
            s.add((String) arr.get(i));
        }
    }

    protected void setMapFromMap(Map<String, String> target, Map<String, String> source) {
        target.clear();
        if (source != null) {
            for (String key : source.keySet()) {
                target.put(key, source.get(key) != null ? source.get(key) : "");
            }
        }
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public Set<String> getAcceptedMediaTypes() {
        return acceptedMediaTypes;
    }

    public void setAcceptedMediaTypes(Set<String> acceptedMediaTypes) {
        this.acceptedMediaTypes.clear();
        if (acceptedMediaTypes != null) {
            this.acceptedMediaTypes.addAll(acceptedMediaTypes);
        }
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        setMapFromMap(this.attributes, attributes);
    }

    public Map<String, String> getMatrixParameters() {
        return matrixParameters;
    }

    public void setMatrixParameters(Map<String, String> matrixParameters) {
        setMapFromMap(this.matrixParameters, matrixParameters);
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    public void setQueryParameters(Map<String, String> queryParameters) {
        setMapFromMap(this.queryParameters, queryParameters);
    }

    public Map<String, String> getFormParameters() {
        return formParameters;
    }

    public void setFormParameters(Map<String, String> formParameters) {
        setMapFromMap(this.formParameters, formParameters);
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

    /**
     * Returns the request body as a Document.
     * <p/>
     * TODO: Get ValidationException to be more informative.
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
            throw new ValidationException();
        } catch (IOException e) {
            throw new ValidationException();
        }
    }

    /**
     * Returns the request body as a JSONObject.
     * <p/>
     * TODO: Get ValidationException to be more informative.
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
            throw new ValidationException();
        }
    }

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