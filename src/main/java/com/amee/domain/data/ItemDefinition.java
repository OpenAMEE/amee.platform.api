package com.amee.domain.data;

import com.amee.base.utils.ThreadBeanHolder;
import com.amee.base.utils.XMLUtils;
import com.amee.domain.*;
import com.amee.domain.algorithm.Algorithm;
import com.amee.domain.environment.Environment;
import com.amee.domain.path.Pathable;
import com.amee.domain.sheet.Choice;
import com.amee.platform.science.InternalValue;
import net.sf.cglib.beans.BeanGenerator;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "ITEM_DEFINITION")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ItemDefinition extends AMEEEntity implements Pathable {

    public final static int NAME_MIN_SIZE = 3;
    public final static int NAME_MAX_SIZE = 255;
    public final static int DRILL_DOWN_MIN_SIZE = 0;
    public final static int DRILL_DOWN_MAX_SIZE = 255;
    public final static int USAGES_MIN_SIZE = 0;
    public final static int USAGES_MAX_SIZE = Metadata.VALUE_MAX_SIZE;

    @Column(name = "NAME", length = NAME_MAX_SIZE, nullable = false)
    private String name = "";

    @Column(name = "DRILL_DOWN", length = DRILL_DOWN_MAX_SIZE, nullable = true)
    private String drillDown = "";

    @OneToMany(mappedBy = "itemDefinition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Algorithm> algorithms = new HashSet<Algorithm>();

    @OneToMany(mappedBy = "itemDefinition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<ItemValueDefinition> itemValueDefinitions = new HashSet<ItemValueDefinition>();

    @OneToMany(mappedBy = "itemDefinition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<ReturnValueDefinition> returnValueDefinitions = new HashSet<ReturnValueDefinition>();

    /**
     * A locally cached List of 'usages'.
     */
    @Transient
    private List<String> usagesList;

    public ItemDefinition() {
        super();
    }

    public ItemDefinition(String name) {
        this();
        setName(name);
    }

    public void add(Algorithm algorithm) {
        algorithms.add(algorithm);
    }

    public void add(ItemValueDefinition itemValueDefinition) {
        itemValueDefinitions.add(itemValueDefinition);
    }

    public void add(ReturnValueDefinition returnValueDefinition) {
        returnValueDefinitions.add(returnValueDefinition);
    }

    public boolean hasDrillDownAvailable() {
        return getDrillDown().length() > 0;
    }

    public List<Choice> getDrillDownChoices() {
        return Choice.parseChoices(getDrillDown());
    }

    /**
     * Check if an {@link ItemValueDefinition} is included in the list of DrillDowns for this ItemDefinition.
     *
     * @param itemValueDefinition - the {@link ItemValueDefinition}
     * @return true if the name is in the DrillDown, otherwise false
     */
    public boolean isDrillDownValue(ItemValueDefinition itemValueDefinition) {
        for (Choice choice : getDrillDownChoices()) {
            if (choice.getName().equalsIgnoreCase(itemValueDefinition.getPath())) {
                return true;
            }
        }
        return false;
    }

    public ItemValueDefinition getItemValueDefinition(String path) {
        for (ItemValueDefinition itemValueDefinition : getItemValueDefinitions()) {
            if (itemValueDefinition.getPath().equalsIgnoreCase(path)) {
                return itemValueDefinition;
            }
        }
        return null;
    }

    public JSONObject getJSONObject() throws JSONException {
        return getJSONObject(true);
    }

    public JSONObject getJSONObject(boolean detailed) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("uid", getUid());
        obj.put("name", getName());
        obj.put("drillDown", getDrillDown());
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

    public Element getElement(Document document, boolean detailed) {
        Element element = document.createElement("ItemDefinition");
        element.setAttribute("uid", getUid());
        element.appendChild(XMLUtils.getElement(document, "Name", getName()));
        element.appendChild(XMLUtils.getElement(document, "DrillDown", getDrillDown()));
        if (detailed) {
            element.setAttribute("created", getCreated().toString());
            element.setAttribute("modified", getModified().toString());
            element.appendChild(Environment.ENVIRONMENT.getIdentityElement(document));
        }
        return element;
    }

    public Element getIdentityElement(Document document) {
        return XMLUtils.getIdentityElement(document, this);
    }

    /**
     * Returns a JavaBean that contains fields and setter/getter methods conforming to
     * the 'fromData' ItemValueDefinitions of this ItemDefinition. The returned object is temporary
     * and transient. It is only intended for use with the validation logic for incoming POST and PUT
     * requests. The bean does not have a permanent class and instead uses a dynamic class produced by
     * a CGLIB {@link BeanGenerator}.
     *
     * @return A JavaBean matching the above description
     */
    public Object getDataItemValuesBean() {
        BeanGenerator bg = new BeanGenerator();
        for (ItemValueDefinition ivd : getActiveItemValueDefinitions()) {
            if (ivd.isFromData()) {
                if (ivd.isDouble()) {
                    bg.addProperty(ivd.getPath(), Double.class);
                } else if (ivd.isInteger()) {
                    bg.addProperty(ivd.getPath(), Integer.class);
                } else {
                    bg.addProperty(ivd.getPath(), String.class);
                }
            }
        }
        return bg.create();
    }

    /**
     * Returns a JavaBean that contains fields and setter/getter methods conforming to
     * the 'fromProfile' ItemValueDefinitions of this ItemDefinition. The returned object is temporary
     * and transient. It is only intended for use with the validation logic for incoming POST and PUT
     * requests. The bean does not have a permanent class and instead uses a dynamic class produced by
     * a CGLIB {@link BeanGenerator}.
     *
     * @return A JavaBean matching the above description
     */
    public Object getProfileItemValuesBean() {
        BeanGenerator bg = new BeanGenerator();
        for (ItemValueDefinition ivd : getActiveItemValueDefinitions()) {
            if (ivd.isFromProfile()) {
                if (ivd.isDouble()) {
                    bg.addProperty(ivd.getPath(), Double.class);
                } else if (ivd.isInteger()) {
                    bg.addProperty(ivd.getPath(), Integer.class);
                } else {
                    bg.addProperty(ivd.getPath(), String.class);
                }
            }
        }
        return bg.create();
    }

    /**
     * Returns a JavaBean that contains fields and setter/getter methods conforming to
     * the 'fromProfile' ItemValueDefinitions of this ItemDefinition. The returned object is temporary
     * and transient. It is only intended for use with the validation logic for incoming POST and PUT
     * requests. The bean does not have a permanent class and instead uses a dynamic class produced by
     * a CGLIB {@link BeanGenerator}.
     *
     * @return A JavaBean matching the above description
     */
    public Object getProfileItemUnitsBean() {
        BeanGenerator bg = new BeanGenerator();
        for (ItemValueDefinition ivd : getActiveItemValueDefinitions()) {
            if (ivd.isFromProfile()) {
                bg.addProperty(ivd.getPath(), String.class);
            }
        }
        return bg.create();
    }

    @Override
    public String getPath() {
        return getUid();
    }

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
        return getPath();
    }

    public void setName(String name) {
        if (name == null) {
            name = "";
        }
        this.name = name;
    }

    public String getDrillDown() {
        return drillDown;
    }

    public void setDrillDown(String drillDown) {
        if (drillDown == null) {
            drillDown = "";
        }
        this.drillDown = drillDown;
    }

    public Set<Algorithm> getAlgorithms() {
        return getActiveAlgorithms();
    }

    public Set<Algorithm> getActiveAlgorithms() {
        Set<Algorithm> activeAlgorithms = new TreeSet<Algorithm>(
                new Comparator<Algorithm>() {
                    public int compare(Algorithm algorithm1, Algorithm algorithm2) {
                        // Comparing by name is incompatible with equals
                        int nameCompare = algorithm1.getName().compareToIgnoreCase(algorithm2.getName());
                        if (nameCompare != 0) {
                            return nameCompare;
                        } else {
                            // If the names are the same we need to fall back to the equals implementation.
                            return algorithm1.getUid().compareTo(algorithm2.getUid());
                        }
                    }
                }
        );
        for (Algorithm algorithm : algorithms) {
            if (!algorithm.isTrash()) {
                activeAlgorithms.add(algorithm);
            }
        }
        return Collections.unmodifiableSet(activeAlgorithms);
    }

    public Set<ItemValueDefinition> getItemValueDefinitions() {
        return getActiveItemValueDefinitions();
    }

    public Set<ItemValueDefinition> getActiveItemValueDefinitions() {
        Set<ItemValueDefinition> activeItemValueDefinitions = new TreeSet<ItemValueDefinition>(
                new Comparator<ItemValueDefinition>() {
                    public int compare(ItemValueDefinition ivd1, ItemValueDefinition ivd2) {
                        // Comparing by name is incompatible with equals
                        int nameCompare = ivd1.getName().compareToIgnoreCase(ivd2.getName());
                        if (nameCompare != 0) {
                            return nameCompare;
                        } else {
                            // If the names are the same we need to fall back to the equals implementation.
                            return ivd1.getUid().compareTo(ivd2.getUid());
                        }
                    }
                }
        );
        for (ItemValueDefinition itemValueDefinition : itemValueDefinitions) {
            if (!itemValueDefinition.isTrash()) {
                activeItemValueDefinitions.add(itemValueDefinition);
            }
        }
        return Collections.unmodifiableSet(activeItemValueDefinitions);
    }

    /**
     * Get a map of {@link ItemValueDefinition}s keyed by their path.
     *
     * @return map of {@link ItemValueDefinition}s
     */
    public Map<String, ItemValueDefinition> getItemValueDefinitionsMap() {
        Map<String, ItemValueDefinition> itemValueDefinitions = new HashMap<String, ItemValueDefinition>();
        for (ItemValueDefinition itemValueDefinition : getActiveItemValueDefinitions()) {
            itemValueDefinitions.put(itemValueDefinition.getPath(), itemValueDefinition);
        }
        return itemValueDefinitions;
    }

    public Set<ReturnValueDefinition> getReturnValueDefinitions() {
        return getActiveReturnValueDefinitions();
    }

    public Set<ReturnValueDefinition> getActiveReturnValueDefinitions() {
        Set<ReturnValueDefinition> activeReturnValueDefinitions = new TreeSet<ReturnValueDefinition>(
                new Comparator<ReturnValueDefinition>() {
                    public int compare(ReturnValueDefinition rvd1, ReturnValueDefinition rvd2) {

                        // Comparing by type is incompatible with equals.
                        int typeCompare = rvd1.getType().compareToIgnoreCase(rvd2.getType());
                        if (typeCompare != 0) {
                            return typeCompare;
                        } else {

                            // If the types are the same we need to fall back to the equals implementation.
                            return rvd1.getUid().compareTo(rvd2.getUid());
                        }
                    }
                }
        );
        for (ReturnValueDefinition returnValueDefinition : returnValueDefinitions) {
            if (!returnValueDefinition.isTrash()) {
                activeReturnValueDefinitions.add(returnValueDefinition);
            }
        }
        return Collections.unmodifiableSet(activeReturnValueDefinitions);
    }

    public ObjectType getObjectType() {
        return ObjectType.ID;
    }

    @Override
    public List<IAMEEEntityReference> getHierarchy() {
        List<IAMEEEntityReference> entities = new ArrayList<IAMEEEntityReference>();
        entities.add(getDataService().getRootDataCategory());
        entities.add(this);
        return entities;
    }

    /**
     * Get the algorithm corresponding to the supplied name.
     *
     * @param name - the name of the Algorithm to retreive
     * @return the Algorithm corresponding to the supplied name
     */
    public Algorithm getAlgorithm(String name) {
        for (Algorithm algorithm : getAlgorithms()) {
            if (algorithm.getName().equalsIgnoreCase(name)) {
                return algorithm;
            }
        }
        return null;
    }

    public void appendInternalValues(Map<ItemValueDefinition, InternalValue> values, APIVersion version) {
        for (ItemValueDefinition ivd : getItemValueDefinitions()) {
            if (ivd.isUsableValue() && ivd.isValidInAPIVersion(version)) {
                values.put(ivd, new InternalValue(ivd));
            }
        }
    }

    /**
     * Returns the usages property as a String.
     *
     * @return String representation of the usages property
     */
    public String getUsagesString() {
        return getMetadataValue("usages");
    }

    public void setUsagesString(String usages) {
        setUsages(usages);
    }

    /**
     * Set the usages property.
     *
     * @param usages value to set
     */
    public void setUsages(String usages) {
        getOrCreateMetadata("usages").setValue(usages);
        usagesList = null;
        onModify();
    }

    /**
     * Returns the usages property as a List. Modifications to the returned list are not
     * persisted and are discouraged.
     *
     * @return a List of usages
     */
    public List<String> getUsages() {
        if (usagesList == null) {
            usagesList = new ArrayList<String>();
            for (String usage : getUsagesString().split(",")) {
                if (!usage.trim().isEmpty()) {
                    usagesList.add(usage.trim());
                }
            }
        }
        return usagesList;
    }

    public List<ItemValueUsage> getItemValueUsages() {
        List<ItemValueUsage> itemValueUsages = new ArrayList<ItemValueUsage>();
        for (String usage : getUsages()) {
            itemValueUsages.add(new ItemValueUsage(usage));
        }
        return itemValueUsages;
    }

    public Set<ItemValueUsage> getAllItemValueUsages() {
        Set<ItemValueUsage> allItemValueUsages = new HashSet<ItemValueUsage>();
        for (ItemValueDefinition itemValueDefinition : getActiveItemValueDefinitions()) {
            allItemValueUsages.addAll(itemValueDefinition.getItemValueUsages());
        }
        return allItemValueUsages;
    }

    /**
     * Updates the usages property with a CSV representation of the usages argument.
     *
     * @param usages List to update the usages property with
     */
    public void setUsages(List<String> usages) {
        if ((usages == null) || usages.isEmpty()) {
            setUsages("");
        } else {
            StringBuilder sb = new StringBuilder();
            for (String usage : usages) {
                if (!usage.trim().isEmpty()) {
                    sb.append(usage);
                    sb.append(",");
                }
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
            setUsages(sb.toString());
        }
    }

    /**
     * Get the modified timestamp for this ItemDefinition and associated ItemValueDefinitions. Will deeply
     * look for the most recent timestamp of either the ItemDefinition or any of the ItemValueDefinitions.
     *
     * @return the most recent modified timestamp.
     */
    public Date getModifiedDeep() {
        Date modified = getModified();
        for (ItemValueDefinition ivd : getActiveItemValueDefinitions()) {
            modified = ivd.getModified().after(modified) ? ivd.getModified() : modified;
        }
        return modified;
    }

    public IDataService getDataService() {
        return ThreadBeanHolder.get(IDataService.class);
    }
}