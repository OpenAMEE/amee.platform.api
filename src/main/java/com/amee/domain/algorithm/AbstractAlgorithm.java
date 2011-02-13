package com.amee.domain.algorithm;

import com.amee.base.utils.XMLUtils;
import com.amee.domain.AMEEEntity;
import com.amee.domain.environment.Environment;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.persistence.*;

@Entity
@Inheritance
@Table(name = "ALGORITHM")
@DiscriminatorColumn(name = "TYPE", length = 3)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public abstract class AbstractAlgorithm extends AMEEEntity {

    public final static int NAME_SIZE = 255;
    // 32767 because this is bigger than 255, smaller than 65535 and fits into an exact number of bits.
    public final static int CONTENT_MAX_SIZE = 32767;

    @Column(name = "NAME", nullable = false, length = NAME_SIZE)
    private String name = "";

    @Column(name = "CONTENT", nullable = true, length = CONTENT_MAX_SIZE)
    private String content = "";

    public AbstractAlgorithm() {
        super();
    }

    public AbstractAlgorithm(String content) {
        this();
        setContent(content);
    }

    public JSONObject getJSONObject() throws JSONException {
        return getJSONObject(true);
    }

    public JSONObject getJSONObject(boolean detailed) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("uid", getUid());
        obj.put("name", getName());
        obj.put("content", getContent());
        if (detailed) {
            obj.put("created", getCreated());
            obj.put("modified", getModified());
            obj.put("environment", Environment.ENVIRONMENT.getIdentityJSONObject());
        }
        return obj;
    }

    public JSONObject getIdentityJSONObject() throws JSONException {
        return XMLUtils.getIdentityJSONObject(this);
    }

    public Element getElement(Document document) {
        return getElement(document, true);
    }

    public abstract String getElementName();

    public Element getElement(Document document, boolean detailed) {
        Element element = document.createElement(getElementName());
        element.setAttribute("uid", getUid());
        element.appendChild(XMLUtils.getElement(document, "Name", getName()));
        element.appendChild(XMLUtils.getElement(document, "Content", getContent()));
        if (detailed) {
            element.setAttribute("created", getCreated().toString());
            element.setAttribute("modified", getModified().toString());
            element.appendChild(Environment.ENVIRONMENT.getIdentityElement(document));
        }
        return element;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) {
            name = "";
        }
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        if (content == null) {
            content = "";
        }
        this.content = content;
    }
}
