package com.amee.domain.data;

import com.amee.base.utils.XMLUtils;
import com.amee.domain.*;
import com.amee.domain.environment.Environment;
import com.amee.domain.path.Pathable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalIdCache;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "data_category")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@NaturalIdCache(region = "query.dataService")
public class DataCategory extends AMEEEntity implements IDataCategoryReference, Pathable {

    public final static int NAME_MIN_SIZE = 2;
    public final static int NAME_MAX_SIZE = 255;
    public final static int PATH_MIN_SIZE = 0;
    public final static int PATH_MAX_SIZE = 255;
    public final static int WIKI_NAME_MIN_SIZE = 3;
    public final static int WIKI_NAME_MAX_SIZE = 255;
    public final static int WIKI_DOC_MAX_SIZE = Metadata.VALUE_MAX_SIZE;
    public final static int PROVENANCE_MAX_SIZE = 255;
    public final static int AUTHORITY_MAX_SIZE = 255;
    public final static int HISTORY_MAX_SIZE = Metadata.VALUE_MAX_SIZE;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "data_category_id")
    private DataCategory dataCategory;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "item_definition_id")
    private ItemDefinition itemDefinition;

    @Column(name = "name", length = NAME_MAX_SIZE, nullable = false)
    private String name = "";

    @Column(name = "path", length = PATH_MAX_SIZE, nullable = false)
    private String path = "";

    @Column(name = "wiki_name", length = WIKI_NAME_MAX_SIZE, nullable = false)
    private String wikiName = "";

    @Transient
    private transient String fullPath;

    public DataCategory() {
        super();
    }

    public DataCategory(String name, String path) {
        this();
        setName(name);
        setPath(path);
    }

    public DataCategory(DataCategory dataCategory) {
        this();
        setDataCategory(dataCategory);
    }

    public DataCategory(DataCategory dataCategory, String name, String path) {
        this(dataCategory);
        setName(name);
        setPath(path);
    }

    public DataCategory(DataCategory dataCategory, String name, String path, ItemDefinition itemDefinition) {
        this(dataCategory, name, path);
        setItemDefinition(itemDefinition);
    }

    public JSONObject getJSONObject() throws JSONException {
        return getJSONObject(true);
    }

    public JSONObject getJSONObject(boolean detailed) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("uid", getUid());
        obj.put("path", getPath());
        obj.put("name", getName());
        obj.put("deprecated", isDeprecated());
        if (detailed) {
            obj.put("created", getCreated().toString());
            obj.put("modified", getModified().toString());
            obj.put("environment", Environment.ENVIRONMENT.getJSONObject(false));
            if (getDataCategory() != null) {
                obj.put("dataCategory", getDataCategory().getIdentityJSONObject());
            }
            if (getItemDefinition() != null) {
                obj.put("itemDefinition", getItemDefinition().getJSONObject());
            }
        }
        return obj;
    }

    public JSONObject getIdentityJSONObject() throws JSONException {
        return getJSONObject(false);
    }

    public Element getElement(Document document, boolean detailed) {
        Element element = document.createElement("DataCategory");
        element.setAttribute("uid", getUid());
        element.appendChild(XMLUtils.getElement(document, "Name", getName()));
        element.appendChild(XMLUtils.getElement(document, "Path", getPath()));
        element.appendChild(XMLUtils.getElement(document, "Deprecated", "" + isDeprecated()));
        if (detailed) {
            element.setAttribute("created", getCreated().toString());
            element.setAttribute("modified", getModified().toString());
            element.appendChild(Environment.ENVIRONMENT.getIdentityElement(document));
            if (getDataCategory() != null) {
                element.appendChild(getDataCategory().getIdentityElement(document));
            }
            if (getItemDefinition() != null) {
                element.appendChild(getItemDefinition().getIdentityElement(document));
            }
        }
        return element;
    }

    public Element getIdentityElement(Document document) {
        return getElement(document, false);
    }

    /**
     * Returns the hierarchy of objects including this object.
     * <p/>
     * Note: This only used in the O&B UI.
     *
     * @return list of entities in hierarchical order
     */
    @Override
    public List<IAMEEEntityReference> getHierarchy() {
        List<IAMEEEntityReference> entities = new ArrayList<IAMEEEntityReference>();
        entities.add(this);
        DataCategory dc = getDataCategory();
        while (dc != null) {
            entities.add(dc);
            dc = dc.getDataCategory();
        }
        Collections.reverse(entities);
        return entities;
    }

    @Override
    public String getDisplayPath() {
        return getPath();
    }

    @Override
    public String getDisplayName() {
        if (getName().length() > 0) {
            return getName();
        } else {
            return getDisplayPath();
        }
    }

    /**
     * Get the full path of this DataCategory.
     *
     * @return the full path
     */
    @Override
    public String getFullPath() {
        // Need to build the fullPath?
        if (fullPath == null) {
            // Is there a parent.
            if (getDataCategory() != null) {
                // There is a parent.
                fullPath = getDataCategory().getFullPath() + "/" + getDisplayPath();
            } else {
                // This is the root.
                fullPath = "";
            }
        }
        return fullPath;
    }

    @Override
    public boolean isItemDefinitionPresent() {
        return getItemDefinition() != null;
    }

    public DataCategory getDataCategory() {
        return dataCategory;
    }

    public void setDataCategory(DataCategory dataCategory) {
        this.dataCategory = dataCategory;
    }

    public ItemDefinition getItemDefinition() {
        if ((itemDefinition != null) && !itemDefinition.isTrash()) {
            return itemDefinition;
        } else {
            return null;
        }
    }

    public void setItemDefinition(ItemDefinition itemDefinition) {
        this.itemDefinition = itemDefinition;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) {
            name = "";
        }
        this.name = name;
    }

    @Override
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        if (path == null) {
            path = "";
        }
        this.path = path;
    }

    @Override
    public String getWikiName() {
        return wikiName;
    }

    public void setWikiName(String wikiName) {
        if (wikiName == null) {
            wikiName = "";
        }
        this.wikiName = wikiName;
    }

    @Override
    public boolean isTrash() {
        return status.equals(AMEEStatus.TRASH) ||
                ((getDataCategory() != null) && getDataCategory().isTrash());
    }

    public String getWikiDoc() {
        return getMetadataValue("wikiDoc");
    }

    public void setWikiDoc(String wikiDoc) {
        getOrCreateMetadata("wikiDoc").setValue(wikiDoc);
        onModify();
    }

    public String getProvenance() {
        return getMetadataValue("provenance");
    }

    public void setProvenance(String provenance) {
        getOrCreateMetadata("provenance").setValue(provenance);
        onModify();
    }

    public String getAuthority() {
        return getMetadataValue("authority");
    }

    public void setAuthority(String authority) {
        getOrCreateMetadata("authority").setValue(authority);
        onModify();
    }

    public String getHistory() {
        return getMetadataValue("history");
    }

    public void setHistory(String history) {
        getOrCreateMetadata("history").setValue(history);
        onModify();
    }

    public String getEcoinventMetaInformation() {
        return getMetadataValue("ecoinventMetaInformation");
    }

    public void setEcoinventMetaInformation(String metaInformation) {
        getOrCreateMetadata("ecoinventMetaInformation").setValue(metaInformation);
        onModify();
    }

    public String getEcoinventDatasetAttributes() {
        return getMetadataValue("ecoinventDatasetAttributes");
    }

    public void setEcoinventDatasetAttributes(String attribtues) {
        getOrCreateMetadata("ecoinventDatasetAttributes").setValue(attribtues);
        onModify();
    }

    @Override
    public void setStatus(AMEEStatus status) {
        this.status = status;
    }

    @Override
    public ObjectType getObjectType() {
        return ObjectType.DC;
    }
}