package com.amee.domain.environment;

import com.amee.base.utils.XMLUtils;
import com.amee.domain.AMEEEntity;
import com.amee.domain.ObjectType;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;

/**
 * @deprecated Environments have been removed from the platform and are only retained for backwards compatibility
 *             in representations.
 */
@Deprecated
public class Environment extends AMEEEntity implements Comparable {

    /**
     * A mock Environment 'entity' so that existing API responses continue to look the same.
     */
    public final static Environment ENVIRONMENT =
            new Environment(
                    2L,
                    "5F5887BCF726",
                    "AMEE",
                    "",
                    "",
                    10,
                    10,
                    new DateTime(2007, 7, 27, 8, 30, 44, 0).toDate(),
                    new DateTime(2007, 7, 27, 8, 30, 44, 0).toDate(),
                    "");

    private String name;
    private String path;
    private String description;
    private String owner;
    private Integer itemsPerPage = 10;
    private Integer itemsPerFeed = 10;

    public Environment() {
        super();
        setName("");
        setPath("");
        setDescription("");
        setOwner("");
        setItemsPerPage(10);
        setItemsPerFeed(10);
    }

    public Environment(String name) {
        this();
        setName(name);
    }

    public Environment(
            Long id,
            String uid,
            String name,
            String path,
            String description,
            Integer itemsPerPage,
            Integer itemsPerFeed,
            Date created,
            Date modified,
            String owner) {
        this();
        setId(id);
        setUid(uid);
        setName(name);
        setPath(path);
        setDescription(description);
        setItemsPerPage(itemsPerPage);
        setItemsPerFeed(itemsPerFeed);
        setCreated(created);
        setModified(modified);
        setOwner(owner);
    }

    public int compareTo(Object o) {
        if (this == o) return 0;
        if (equals(o)) return 0;
        Environment environment = (Environment) o;
        return getUid().compareTo(environment.getUid());
    }

    public JSONObject getJSONObject() throws JSONException {
        return getJSONObject(true);
    }

    public JSONObject getJSONObject(boolean detailed) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("uid", getUid());
        obj.put("name", getName());
        obj.put("path", getPath());
        obj.put("description", getDescription());
        obj.put("owner", getOwner());
        obj.put("itemsPerPage", getItemsPerPage());
        obj.put("itemsPerFeed", getItemsPerFeed());
        if (detailed) {
            obj.put("created", getCreated().toString());
            obj.put("modified", getModified().toString());
        }
        return obj;
    }

    public JSONObject getIdentityJSONObject() throws JSONException {
        return XMLUtils.getIdentityJSONObject(this);
    }

    public Element getElement(Document document) {
        return getElement(document, true);
    }

    public Element getElement(Document document, boolean detailed) {
        Element element = document.createElement("Environment");
        element.setAttribute("uid", getUid());
        element.appendChild(XMLUtils.getElement(document, "Name", getName()));
        element.appendChild(XMLUtils.getElement(document, "Path", getPath()));
        element.appendChild(XMLUtils.getElement(document, "Description", getDescription()));
        element.appendChild(XMLUtils.getElement(document, "Owner", getOwner()));
        element.appendChild(XMLUtils.getElement(document, "ItemsPerPage", getItemsPerPage().toString()));
        element.appendChild(XMLUtils.getElement(document, "ItemsPerFeed", getItemsPerFeed().toString()));
        if (detailed) {
            element.setAttribute("created", getCreated().toString());
            element.setAttribute("modified", getModified().toString());
        }
        return element;
    }

    public Element getIdentityElement(Document document) {
        return XMLUtils.getIdentityElement(document, this);
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        if (path == null) {
            path = "";
        }
        this.path = path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null) {
            description = "";
        }
        this.description = description;
    }

    public Integer getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(Integer itemsPerPage) {
        if (itemsPerPage != null) {
            this.itemsPerPage = itemsPerPage;
        }
    }

    public void setItemsPerPage(String itemsPerPage) {
        try {
            setItemsPerPage(Integer.parseInt(itemsPerPage));
        } catch (NumberFormatException e) {
            // swallow
        }
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        if (owner == null) {
            owner = "";
        }
        this.owner = owner;
    }

    public Integer getItemsPerFeed() {
        return itemsPerFeed;
    }

    public void setItemsPerFeed(Integer itemsPerFeed) {
        this.itemsPerFeed = itemsPerFeed;
    }

    public void setItemsPerFeed(String itemsPerFeed) {
        try {
            setItemsPerFeed(Integer.parseInt(itemsPerFeed));
        } catch (NumberFormatException e) {
            // swallow
        }
    }

    public ObjectType getObjectType() {
        return ObjectType.ENV;
    }
}