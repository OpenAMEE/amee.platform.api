package com.amee.platform.resource.datacategory.v_3_0;

import com.amee.base.domain.Since;
import com.amee.platform.resource.datacategory.v_3_3.DataCategoryValidationHelper_3_3_0;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@Scope("prototype")
@Since("3.0.0")
public class DataCategoryValidationHelper_3_0_0 extends DataCategoryValidationHelper_3_3_0 {

    /**
     * Overrides the super-class method to remove the history field.
     */
    @Override
    public String[] getAllowedFields() {
        if (allowedFields == null) {
            allowedFields = new HashSet<String>();
            allowedFields.add("name");
            allowedFields.add("path");
            allowedFields.add("wikiName");
            allowedFields.add("wikiDoc");
            allowedFields.add("provenance");
            allowedFields.add("authority");
            allowedFields.add("dataCategory");
        }
        return allowedFields.toArray(new String[]{});
    }
}
