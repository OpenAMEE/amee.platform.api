/**
 * This file is part of AMEE.
 *
 * AMEE is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * AMEE is free software and is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Created by http://www.dgen.net.
 * Website http://www.amee.cc
 */
package com.amee.domain.data;

import com.amee.base.utils.XMLUtils;
import com.amee.domain.*;
import com.amee.domain.data.builder.v2.ItemValueDefinitionBuilder;
import com.amee.domain.sheet.Choice;
import com.amee.platform.science.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Configurable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.annotation.Resource;
import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "ITEM_VALUE_DEFINITION")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Configurable(autowire = Autowire.BY_TYPE)
public class ItemValueDefinition extends AMEEEnvironmentEntity implements ExternalValue {

    public final static int NAME_MIN_SIZE = 2;
    public final static int NAME_MAX_SIZE = 255;
    public final static int PATH_MIN_SIZE = 2;
    public final static int PATH_MAX_SIZE = 255;
    public final static int VALUE_MAX_SIZE = 255;
    public final static int CHOICES_MAX_SIZE = 255;
    public final static int ALLOWED_ROLES_MAX_SIZE = 255;
    public final static int WIKI_DOC_MIN_SIZE = 0;
    public final static int WIKI_DOC_MAX_SIZE = Metadata.VALUE_SIZE;

    @Transient
    @Resource
    private IMetadataService metadataService;

    @Transient
    @Resource
    private ILocaleService localeService;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "ITEM_DEFINITION_ID")
    private ItemDefinition itemDefinition;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "VALUE_DEFINITION_ID")
    private ValueDefinition valueDefinition;

    @Column(name = "UNIT")
    private String unit;

    @Column(name = "PER_UNIT")
    private String perUnit;

    @Column(name = "NAME", length = NAME_MAX_SIZE, nullable = false)
    private String name = "";

    @Column(name = "PATH", length = PATH_MAX_SIZE, nullable = false)
    @Index(name = "PATH_IND")
    private String path = "";

    @Column(name = "VALUE", length = VALUE_MAX_SIZE, nullable = true)
    private String value = "";

    // Comma separated key/value pairs. Value is key if key not supplied. Example: "key=value,key=value"
    @Column(name = "CHOICES")
    private String choices = "";

    @Column(name = "FROM_PROFILE")
    @Index(name = "FROM_PROFILE_IND")
    private boolean fromProfile = false;

    @Column(name = "FROM_DATA")
    @Index(name = "FROM_PROFILE_IND")
    private boolean fromData = false;

    @Column(name = "ALLOWED_ROLES", length = ALLOWED_ROLES_MAX_SIZE, nullable = true)
    private String allowedRoles = "";

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "ITEM_VALUE_DEFINITION_API_VERSION",
            joinColumns = {@JoinColumn(name = "ITEM_VALUE_DEFINITION_ID")},
            inverseJoinColumns = {@JoinColumn(name = "API_VERSION_ID")})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<APIVersion> apiVersions = new HashSet<APIVersion>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ALIASED_TO_ID")
    private ItemValueDefinition aliasedTo = null;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ALIASED_TO_ID")
    private List<ItemValueDefinition> aliases = new ArrayList<ItemValueDefinition>();

    @Column(name = "FORCE_TIMESERIES")
    private boolean isForceTimeSeries;

    /**
     * A String containing an arbitrary sized JSON object containing configuration data for the
     * ItemValueDefinition. Primary use will be for validation and 'usages'. This will be stored in
     * a TEXT column in the database.
     */
    @Column(name = "CONFIGURATION")
    private String configuration = "";

    /**
     * A JSONObject based on the deserialized form of the configuration property.
     */
    @Transient
    private JSONObject configurationObj;

    @Transient
    private Map<String, Metadata> metadatas = new HashMap<String, Metadata>();

    @Transient
    private Builder builder;

    public ItemValueDefinition() {
        super();
    }

    @Override
    public boolean isTrash() {
        return status.equals(AMEEStatus.TRASH) || itemDefinition.isTrash();
    }

    public ItemValueDefinition(ItemDefinition itemDefinition) {
        super(itemDefinition.getEnvironment());
        setItemDefinition(itemDefinition);
        itemDefinition.add(this);
    }

    public ItemValueDefinition(ItemDefinition itemDefinition, String name) {
        this(itemDefinition);
        setName(name);
    }

    public boolean isUsableValue() {
        return getValue() != null && !getValue().isEmpty();
    }

    public boolean isChoicesAvailable() {
        return getChoices().length() > 0;
    }

    public List<Choice> getChoiceList() {
        return Choice.parseChoices(getChoices());
    }

    public JSONObject getJSONObject() throws JSONException {
        return getJSONObject(true);
    }

    public JSONObject getJSONObject(boolean detailed) throws JSONException {
        return getBuilder().getJSONObject(detailed);
    }

    public JSONObject getIdentityJSONObject() throws JSONException {
        return XMLUtils.getIdentityJSONObject(this);
    }

    public Element getElement(Document document) {
        return getElement(document, true);
    }

    public Element getElement(Document document, boolean detailed) {
        return getBuilder().getElement(document, detailed);
    }

    public Element getIdentityElement(Document document) {
        return XMLUtils.getIdentityElement(document, this);
    }

    public ItemDefinition getItemDefinition() {
        return itemDefinition;
    }

    public void setItemDefinition(ItemDefinition itemDefinition) {
        this.itemDefinition = itemDefinition;
    }

    public ValueDefinition getValueDefinition() {
        return valueDefinition;
    }

    public void setValueDefinition(ValueDefinition valueDefinition) {
        this.valueDefinition = valueDefinition;
    }

    public String getName() {
        return localeService.getLocaleNameValue(this, name);
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

    public String getValue() {
        return value;
    }

    public String getUsableValue() {
        return getValue();
    }

    public void setValue(String value) {
        if (value == null) {
            value = "";
        }
        this.value = value;
    }

    public String getChoices() {
        return choices;
    }

    public void setChoices(String choices) {
        if (choices == null) {
            choices = "";
        }
        this.choices = choices;
    }

    public boolean isFromProfile() {
        return fromProfile;
    }

    public void setFromProfile(boolean fromProfile) {
        this.fromProfile = fromProfile;
    }

    public boolean isFromData() {
        return fromData;
    }

    public void setFromData(boolean fromData) {
        this.fromData = fromData;
    }

    /**
     * Check if this ItemValueDefinition is included in the list of DrillDowns for it's ItemDefinition.
     *
     * @return true if it is in the DrillDown, otherwise false
     */
    public boolean isDrillDown() {
        return this.itemDefinition.isDrillDownValue(this);
    }

    public String getAllowedRoles() {
        return allowedRoles;
    }

    public void setAllowedRoles(String allowedRoles) {
        if (allowedRoles == null) {
            allowedRoles = "";
        }
        this.allowedRoles = allowedRoles;
    }

    public void setPerUnit(String perUnit) {
        this.perUnit = perUnit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public AmountUnit getUnit() {
        return (unit != null) ? AmountUnit.valueOf(unit) : AmountUnit.ONE;
    }

    public AmountUnit getCanonicalUnit() {
        return getUnit();
    }

    public AmountPerUnit getPerUnit() {
        return (perUnit != null) ? AmountPerUnit.valueOf(perUnit) : AmountPerUnit.ONE;
    }

    public AmountPerUnit getCanonicalPerUnit() {
        return getPerUnit();
    }

    public boolean hasUnit() {
        return unit != null;
    }

    public boolean hasPerUnit() {
        return perUnit != null;
    }

    public boolean isValidUnit(String unit) {
        return getUnit().isCompatibleWith(unit);
    }

    public boolean isValidPerUnit(String perUnit) {
        return getPerUnit().isCompatibleWith(perUnit);
    }

    public AmountCompoundUnit getCompoundUnit() {
        return getUnit().with(getPerUnit());
    }

    public AmountCompoundUnit getCanonicalCompoundUnit() {
        if (aliasedTo != null) {
            return aliasedTo.getCompoundUnit();
        } else {
            return getCompoundUnit();
        }
    }

    public Set<APIVersion> getAPIVersions() {
        return apiVersions;
    }

    public void setAPIVersions(Set<APIVersion> apiVersions) {
        this.apiVersions = apiVersions;
    }

    public boolean isValidInAPIVersion(APIVersion apiVersion) {
        return apiVersions.contains(apiVersion);
    }

    public boolean addAPIVersion(APIVersion apiVersion) {
        return apiVersions.add(apiVersion);
    }

    public boolean removeAPIVersion(APIVersion apiVersion) {
        return apiVersions.remove(apiVersion);
    }

    public ItemValueDefinition getAliasedTo() {
        return aliasedTo;
    }

    public void setAliasedTo(ItemValueDefinition ivd) {
        this.aliasedTo = ivd;
    }

    public List<ItemValueDefinition> getAliases() {
        return aliases;
    }

    public String getCanonicalPath() {
        if (aliasedTo != null) {
            return aliasedTo.getPath();
        } else {
            return getPath();
        }
    }

    public String getCanonicalName() {
        if (aliasedTo != null) {
            return aliasedTo.getName();
        } else {
            return getName();
        }
    }

    /**
     * Does this represent a double value.
     *
     * @return true if this value represents a double value, otherwise false
     *         <p/>
     *         {@see ValueType.DOUBLE}
     */
    public boolean isDouble() {
        return getValueDefinition().getValueType().equals(ValueType.DOUBLE);
    }

    /**
     * Does this represent a text value.
     *
     * @return true if this value represents a text value, otherwise false
     *         <p/>
     *         {@see ValueType.TEXT}
     */
    public boolean isText() {
        return getValueDefinition().getValueType().equals(ValueType.TEXT);
    }

    /**
     * Does this represent a date value.
     *
     * @return true if this value represents a date value, otherwise false
     *         <p/>
     *         {@see ValueType.DATE}
     */
    public boolean isDate() {
        return getValueDefinition().getValueType().equals(ValueType.DATE);
    }

    public void setBuilder(Builder builder) {
        this.builder = builder;
    }

    public Builder getBuilder() {
        if (builder == null) {
            setBuilder(new ItemValueDefinitionBuilder(this));
        }
        return builder;
    }

    /**
     * Returns whether or not this ItemValueDefinition should always be g
     *
     * @return true if this ItemValueDefinition should always be treated as a timeseries value
     */
    public boolean isForceTimeSeries() {
        return isForceTimeSeries;
    }

    public void setForceTimeSeries(boolean isForceTimeSeries) {
        this.isForceTimeSeries = isForceTimeSeries;
    }

    /**
     * Get the configuration property.
     *
     * @return the configuration value
     */
    public String getConfigurationString() {
        return configuration;
    }

    /**
     * Get the configuration property. The returned JSONObject should not be modified (but any modifications
     * will never be persisted).
     *
     * @return the configuration value
     */
    public JSONObject getConfiguration() {
        if (configurationObj == null) {
            try {
                configurationObj = new JSONObject(getConfigurationString());
            } catch (JSONException e) {
                // This should never happen as the various configuration methods are protective.
                throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
            }
        }
        return configurationObj;
    }

    /**
     * Set the configuration property.
     *
     * @param configuration String value to set
     */
    public void setConfiguration(String configuration) {
        if (configuration == null) {
            configuration = "{}";
        }
        try {
            configurationObj = new JSONObject(configuration);
            this.configuration = configurationObj.toString();
        } catch (JSONException e) {
            throw new IllegalArgumentException("The configuration argument was not valid JSON.");
        }
    }

    /**
     * Set the configuration property.
     *
     * @param configuration JSONObject value to set
     */
    public void setConfiguration(JSONObject configuration) {
        if (configuration == null) {
            configuration = new JSONObject();
        }
        setConfiguration(configuration.toString());
    }

    /**
     * Return a Set of ItemValueUsages extracted from the configuration property. The Set and contained
     * ItemValueUsage instances are created for every invocation. The configuration property is
     * expected to contain a 'usages' JSON array. The Set will be empty if this array does not exist
     * or is empty.
     *
     * @return Set of ItemValueUsages.
     */
    public Set<ItemValueUsage> getItemValueUsages() {
        try {
            if (getConfiguration().has("usages")) {
                return ItemValueUsage.getItemValueUsages(getConfiguration().getJSONArray("usages"));
            } else {
                return new HashSet<ItemValueUsage>();
            }
        } catch (JSONException e) {
            // This should never happen as the various configuration methods are protective.
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    public String getWikiDoc() {
        return getMetadataValue("wikiDoc");
    }

    public void setWikiDoc(String wikiDoc) {
        getOrCreateMetadata("wikiDoc").setValue(wikiDoc);
        onModify();
    }

    // TODO: The following three methods are cut-and-pasted between various entities. They should be consolidated.

    private Metadata getMetadata(String key) {
        if (!metadatas.containsKey(key)) {
            metadatas.put(key, metadataService.getMetadataForEntity(this, key));
        }
        return metadatas.get(key);
    }

    private String getMetadataValue(String key) {
        Metadata metadata = getMetadata(key);
        if (metadata != null) {
            return metadata.getValue();
        } else {
            return "";
        }
    }

    protected Metadata getOrCreateMetadata(String key) {
        Metadata metadata = getMetadata(key);
        if (metadata == null) {
            metadata = new Metadata(this, key);
            metadataService.persist(metadata);
            metadatas.put(key, metadata);
        }
        return metadata;
    }

    public String getLabel() {
        return getItemDefinition().getName() + "/" + getPath();
    }

    /**
     * As an ExternalValue, an ItemValueDefinition never has a startDate.
     *
     * @return null, always
     */
    public StartEndDate getStartDate() {
        return null;
    }

    /**
     * As an ExternalValue, an ItemValueDefinition is NOT convertible. It cannot be converted, at runtime, from one unit to another.
     *
     * @return false, always
     */
    public boolean isConvertible() {
        return false;
    }

    public ObjectType getObjectType() {
        return ObjectType.IVD;
    }
}