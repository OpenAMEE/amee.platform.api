package com.amee.domain.data;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.Transient;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Configurable(autowire = Autowire.BY_TYPE)
public class ItemValueMap extends HashMap {

    private HashMap legacyMap;
    private HashMap nuMap;

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
        if (legacyMap != null) {
            return true;
        } else if (nuMap != null) {
            return false;
        } else {
            throw new IllegalStateException("Missing map.");
        }
    }

    public LegacyItemValue get(String path) {
        return null;
    }

    public List<LegacyItemValue> getAll(Date startDate) {
        return null;
    }

    public List<LegacyItemValue> getAll(String path) {
        return null;
    }

    public LegacyItemValue get(String path, Date startDate) {
        return null;
    }

    public void put(String path, LegacyItemValue itemValue) {

    }

    public int compare(LegacyItemValue iv1, LegacyItemValue iv2) {
        return 0;
    }
}
