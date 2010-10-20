package com.amee.domain.data;

import com.amee.domain.item.BaseItemValue;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.Transient;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Configurable(autowire = Autowire.BY_TYPE)
public class ItemValueMap extends HashMap {

    private LegacyItemValueMap legacyMap;
    private NuItemValueMap nuMap;

    @Transient
    private transient ItemValueMap adapter;

    public ItemValueMap(LegacyItemValueMap legacyMap) {
        super();
        setLegacyMap(legacyMap);
    }

    public ItemValueMap(NuItemValueMap nuMap) {
        super();
        setNuMap(nuMap);
    }

    private void setLegacyMap(LegacyItemValueMap legacyMap) {
        legacyMap.setAdapter(this);
        this.legacyMap = legacyMap;
    }

    private void setNuMap(NuItemValueMap nuMap) {
        nuMap.setAdapter(this);
        this.nuMap = nuMap;
    }

    public static ItemValueMap getItemValueMap(LegacyItemValueMap map) {
        if (map != null) {
            if (map.getAdapter() != null) {
                return map.getAdapter();
            } else {
                return new ItemValueMap(map);
            }
        } else {
            return null;
        }
    }

    public static ItemValueMap getItemValueMap(NuItemValueMap map) {
        if (map != null) {
            if (map.getAdapter() != null) {
                return map.getAdapter();
            } else {
                return new ItemValueMap(map);
            }
        } else {
            return null;
        }
    }

    public boolean isLegacy() {
        if (getLegacyMap() != null) {
            return true;
        } else if (getNuMap() != null) {
            return false;
        } else {
            throw new IllegalStateException("Missing map.");
        }
    }

    public ItemValue get(String path) {
        if (isLegacy()) {
             return getLegacyMap().get(path).getAdapter();
        } else {
            return getNuMap().get(path).getAdapter();
        }
    }

    public List<ItemValue> getAll(Date startDate) {
        if (isLegacy()) {
            return legacyToAdapter(getLegacyMap().getAll(startDate));
        } else {
            return nuToAdapter(getNuMap().getAll(startDate));
        }
    }

    public List<ItemValue> getAll(String path) {
        if (isLegacy()) {
            return legacyToAdapter(getLegacyMap().getAll(path));
        } else {
            return nuToAdapter(getNuMap().getAll(path));
        }
    }

    public ItemValue get(String path, Date startDate) {
        if (isLegacy()) {
            return getLegacyMap().get(path, startDate).getAdapter();
        } else {
            return getNuMap().get(path, startDate).getAdapter();
        }
    }

    // TODO: Unsure how to deal with this
    public void put(String path, LegacyItemValue itemValue) {
        if (isLegacy()) {
            getLegacyMap().put(path, itemValue);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    // TODO: Unsure how to deal with this
    public int compare(LegacyItemValue iv1, LegacyItemValue iv2) {
        return 0;
    }

    private List<ItemValue> legacyToAdapter(List<LegacyItemValue> legacyItemValues) {
        return (List<ItemValue>) CollectionUtils.collect(
                legacyItemValues, new Transformer() {
                    public Object transform(Object legacyItemValue) {
                        return ((LegacyItemValue)legacyItemValue).getAdapter();
                    }
                });
    }

    private List<ItemValue> nuToAdapter(List<BaseItemValue> baseItemValues) {
        return (List<ItemValue>) CollectionUtils.collect(
                baseItemValues, new Transformer() {
                    public Object transform(Object baseItemValue) {
                        return ((BaseItemValue)baseItemValue).getAdapter();
                    }
                });
    }    

    public LegacyItemValueMap getLegacyMap() {
        return legacyMap;
    }

    public NuItemValueMap getNuMap() {
        return nuMap;
    }

    public ItemValueMap getAdapter() {
        return adapter;
    }

    
}
