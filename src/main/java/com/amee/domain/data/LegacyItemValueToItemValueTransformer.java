package com.amee.domain.data;

import org.apache.commons.collections.Transformer;

public class LegacyItemValueToItemValueTransformer implements Transformer {

    private static final Transformer INSTANCE = new LegacyItemValueToItemValueTransformer();

    public static Transformer getInstance() {
        return INSTANCE;
    }

    private LegacyItemValueToItemValueTransformer() {
        super();
    }

    @Override
    public Object transform(Object input) {
        return ItemValue.getItemValue((LegacyItemValue) input);
    }
}