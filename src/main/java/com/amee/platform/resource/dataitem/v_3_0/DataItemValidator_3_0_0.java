package com.amee.platform.resource.dataitem.v_3_0;

import com.amee.base.domain.Since;
import com.amee.platform.resource.dataitem.v_3_4.DataItemValidator_3_4_0;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashSet;

/**
 * DataItem validation. Extends DataItemValidator_3_4_0 but only supports name, path, wikiDoc and provenance.
 */
@Service
@Scope("prototype")
@Since("3.0.0")
public class DataItemValidator_3_0_0 extends DataItemValidator_3_4_0 {

    public DataItemValidator_3_0_0() {
        super();
        addName();
        addPath();
        addWikiDoc();
        addProvenance();
    }

    @Override
    public String[] getAllowedFields() {
        if (allowedFields == null) {
            allowedFields = new HashSet<String>();
            allowedFields.add("name");
            allowedFields.add("path");
            allowedFields.add("wikiDoc");
            allowedFields.add("provenance");
        }
        return allowedFields.toArray(new String[]{});
    }
}
