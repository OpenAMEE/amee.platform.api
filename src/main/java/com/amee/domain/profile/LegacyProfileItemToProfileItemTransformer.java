package com.amee.domain.profile;

import org.apache.commons.collections.Transformer;

public class LegacyProfileItemToProfileItemTransformer implements Transformer {

    private static final Transformer INSTANCE = new LegacyProfileItemToProfileItemTransformer();

    public static Transformer getInstance() {
        return INSTANCE;
    }

    private LegacyProfileItemToProfileItemTransformer() {
        super();
    }

    @Override
    public Object transform(Object input) {
        return ProfileItem.getProfileItem((LegacyProfileItem) input);
    }
}