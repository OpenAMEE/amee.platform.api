package com.amee.domain.data;

import javax.persistence.Transient;
import java.util.HashMap;

public class NuItemValueMap extends HashMap {

    @Transient
    private transient ItemValueMap adapter;

    public void setAdapter(ItemValueMap adapter) {
        this.adapter = adapter;
    }

}
