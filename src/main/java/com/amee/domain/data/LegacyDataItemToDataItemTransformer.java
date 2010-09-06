package com.amee.domain.data;

import org.apache.commons.collections.Transformer;

public class LegacyDataItemToDataItemTransformer implements Transformer {

    private static final Transformer INSTANCE = new LegacyDataItemToDataItemTransformer();

    public static Transformer getInstance() {
        return INSTANCE;
    }

    private LegacyDataItemToDataItemTransformer() {
        super();
    }

    @Override
    public Object transform(Object input) {
        return DataItem.getDataItem((LegacyDataItem) input);
    }
}