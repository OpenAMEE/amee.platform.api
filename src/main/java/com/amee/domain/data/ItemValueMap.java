package com.amee.domain.data;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.Transient;
import java.util.HashMap;

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

}
