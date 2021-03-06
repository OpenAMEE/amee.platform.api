package com.amee.domain.data;

import com.amee.base.utils.XMLUtils;
import com.amee.domain.*;
import com.amee.domain.data.builder.v2.ItemValueDefinitionBuilder;
import com.amee.domain.path.Pathable;
import com.amee.domain.sheet.Choice;
import com.amee.platform.science.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "item_value_definition")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ItemValueDefinition extends AMEEEntity implements ExternalValue, Pathable {

    public static final int NAME_MIN_SIZE = 2;
    public static final int NAME_MAX_SIZE = 255;
    public static final int PATH_MIN_SIZE = 2;
    public static final int PATH_MAX_SIZE = 255;
    public static final int VALUE_MAX_SIZE = 255;
    public static final int ALLOWED_ROLES_MAX_SIZE = 255;
    public static final int WIKI_DOC_MAX_SIZE = Metadata.VALUE_MAX_SIZE;
    public static final int UNIT_MAX_SIZE = 255;
    public static final int PER_UNIT_MAX_SIZE = 255;
    public static final int CHOICES_MAX_SIZE = 255;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "item_definition_id")
    private ItemDefinition itemDefinition;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "value_definition_id")
    private ValueDefinition valueDefinition;

    @Column(name = "unit")
    private String unit = "";

    @Column(name = "per_unit")
    private String perUnit = "";

    @Column(name = "name", length = NAME_MAX_SIZE, nullable = false)
    private String name = "";

    @Column(name = "path", length = PATH_MAX_SIZE, nullable = false)
    private String path = "";

    @Column(name = "value", length = VALUE_MAX_SIZE, nullable = true)
    private String value = "";

    // Comma separated key/value pairs. Value is key if key not supplied. Example: "key=value,key=value"
    @Column(name = "choices")
    private String choices = "";

    @Column(name = "from_profile")
    private boolean fromProfile = false;

    @Column(name = "from_data")
    private boolean fromData = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "item_value_definition_api_version",
            joinColumns = {@JoinColumn(name = "item_value_definition_id")},
            inverseJoinColumns = {@JoinColumn(name = "api_version_id")})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<APIVersion> apiVersions = new HashSet<APIVersion>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aliased_to_id")
    private ItemValueDefinition aliasedTo = null;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "aliased_to_id")
    private List<ItemValueDefinition> aliases = new ArrayList<ItemValueDefinition>();

    /**
     * A JSONObject based on the deserialized form of the configuration property.
     */
    @Transient
    private JSONObject configurationObj;

    @Transient
    private Builder builder;

    public ItemValueDefinition() {
        super();
    }

    public ItemValueDefinition(ItemDefinition itemDefinition) {
        this();
        setItemDefinition(itemDefinition);
        itemDefinition.add(this);
    }

    public ItemValueDefinition(ItemDefinition itemDefinition, String name) {
        this(itemDefinition);
        setName(name);
    }

    /**
     * An ItemValueDefinition is considered trashed if itself is trashed, its item definition is trashed or
     * its value definition is trashed.
     *
     * @return true if this ItemValueDefinition should be considered trashed.
     */
    @Override
    public boolean isTrash() {
        return status.equals(AMEEStatus.TRASH) || getItemDefinition().isTrash() || getValueDefinition().isTrash();
    }

    public boolean isUsableValue() {
        return !StringUtils.isBlank(getValue());
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

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayName() {
        return getName();
    }

    @Override
    public String getDisplayPath() {
        return getPath();
    }

    @Override
    public String getFullPath() {
        return getItemDefinition().getFullPath() + "/" + getPath();
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

    public String getValue() {
        return value;
    }

    @Override
    public Double getValueAsDouble() {
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Value is not a double");
        }
    }

    @Override
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

    public void setPerUnit(AmountPerUnit perUnit) {
        this.perUnit = perUnit.toString();
    }

    public void setPerUnit(String perUnit) {
        if (perUnit == null) {
            perUnit = "";
        }
        this.perUnit = perUnit;
    }

    public void setUnit(AmountUnit unit) {
        this.unit = unit.toString();
    }

    public void setUnit(String unit) {
        if (unit == null) {
            unit = "";
        }
        this.unit = unit;
    }

    @Override
    public AmountUnit getUnit() {
        return hasUnit() ? AmountUnit.valueOf(unit) : AmountUnit.ONE;
    }

    @Override
    public AmountUnit getCanonicalUnit() {
        return getUnit();
    }

    @Override
    public AmountPerUnit getPerUnit() {
        return hasPerUnit() ? AmountPerUnit.valueOf(perUnit) : AmountPerUnit.ONE;
    }

    @Override
    public AmountPerUnit getCanonicalPerUnit() {
        return getPerUnit();
    }

    @Override
    public boolean hasUnit() {
        return StringUtils.isNotBlank(unit);
    }

    @Override
    public boolean hasPerUnit() {
        return StringUtils.isNotBlank(perUnit);
    }

    public boolean isValidUnit(String unit) {
        return getUnit().isCompatibleWith(unit);
    }

    public boolean isValidPerUnit(String perUnit) {
        return getPerUnit().isCompatibleWith(perUnit);
    }

    @Override
    public AmountCompoundUnit getCompoundUnit() {
        return getUnit().with(getPerUnit());
    }

    @Override
    public AmountCompoundUnit getCanonicalCompoundUnit() {
        if (aliasedTo != null) {
            return aliasedTo.getCompoundUnit();
        } else {
            return getCompoundUnit();
        }
    }

    public Set<APIVersion> getApiVersions() {
        return apiVersions;
    }

    public void setApiVersions(Set<APIVersion> apiVersions) {
        this.apiVersions.clear();
        if (apiVersions != null) {
            this.apiVersions.addAll(apiVersions);
        }
    }

    public boolean isValidInAPIVersion(APIVersion apiVersion) {
        return apiVersions.contains(apiVersion);
    }

    public boolean addAPIVersion(APIVersion apiVersion) {
        return (apiVersion != null) && apiVersions.add(apiVersion);
    }

    public boolean removeAPIVersion(APIVersion apiVersion) {
        return (apiVersion != null) && apiVersions.remove(apiVersion);
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
    @Override
    public boolean isDouble() {
        return getValueDefinition().getValueType().equals(ValueType.DOUBLE);
    }

    /**
     * Does this represent an integer value.
     *
     * @return true if this value represents a integer value, otherwise false
     *         <p/>
     *         {@see ValueType.INTEGER}
     */
    public boolean isInteger() {
        return getValueDefinition().getValueType().equals(ValueType.INTEGER);
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
     * Get the configuration property.
     *
     * @return the configuration value
     */
    public String getConfigurationString() {
        return getMetadataValue("configuration");
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
                String configuration = getConfigurationString();
                if (!configuration.isEmpty()) {
                    configurationObj = new JSONObject(configuration);
                } else {
                    configurationObj = new JSONObject();
                }
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
        try {
            if (configuration == null) {
                configuration = "{}";
            }
            configurationObj = new JSONObject(configuration);
            getOrCreateMetadata("configuration").setValue(configurationObj.toString());
            onModify();
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
                return ItemValueUsage.deserialize(getConfiguration().getJSONArray("usages"));
            } else {
                return new HashSet<ItemValueUsage>();
            }
        } catch (JSONException e) {
            // This should never happen as the various configuration methods are protective.
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    /**
     * Replaces the configuration 'usages' block with a serialization of the supplied
     * Set of ItemValueUsages.
     *
     * @param itemValueUsages to place in the configuration
     */
    public void setItemValueUsages(Set<ItemValueUsage> itemValueUsages) {
        try {
            JSONObject configuration = getConfiguration();
            configuration.put("usages", ItemValueUsage.serialize(itemValueUsages));
            setConfiguration(configuration);
        } catch (JSONException e) {
            // Should never happen...
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

    @Override
    public String getLabel() {
        return getItemDefinition().getName() + "/" + getPath();
    }

    /**
     * As an ExternalValue, an ItemValueDefinition never has a startDate.
     *
     * @return null, always
     */
    @Override
    public StartEndDate getStartDate() {
        return null;
    }

    /**
     * As an ExternalValue, an ItemValueDefinition is NOT convertible. It cannot be converted, at runtime, from one unit to another.
     *
     * @return false, always
     */
    @Override
    public boolean isConvertible() {
        return false;
    }

    @Override
    public ObjectType getObjectType() {
        return ObjectType.IVD;
    }

    @Override
    public List<IAMEEEntityReference> getHierarchy() {
        List<IAMEEEntityReference> entities = getItemDefinition().getHierarchy();
        entities.add(this);
        return entities;
    }
}