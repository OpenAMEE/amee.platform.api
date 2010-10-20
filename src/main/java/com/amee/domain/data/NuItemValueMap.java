package com.amee.domain.data;

import com.amee.domain.item.BaseItemValue;

import javax.persistence.Transient;
import java.util.HashMap;

public class NuItemValueMap extends HashMap {

    @Transient
    private transient ItemValueMap adapter;

    public BaseItemValue get(String path) {
        return null;
    }

    public ItemValueMap getAdapter() {
        return adapter;
    }

    public void setAdapter(ItemValueMap adapter) {
        this.adapter = adapter;
    }

    public Object fail() {
        throw new UnsupportedOperationException();
    }


}
