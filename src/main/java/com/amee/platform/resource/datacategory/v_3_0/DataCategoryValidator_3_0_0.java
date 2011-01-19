package com.amee.platform.resource.datacategory.v_3_0;

import com.amee.base.domain.Since;
import com.amee.platform.resource.datacategory.v_3_3.DataCategoryValidator_3_3_0;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.0.0")
public class DataCategoryValidator_3_0_0 extends DataCategoryValidator_3_3_0 {

    public DataCategoryValidator_3_0_0() {
        super();
    }

    /**
     * Overrides the super-class method to remove the history field.
     */
    @Override
    protected void initialise() {
        addName();
        addPath();
        addWikiName();
        addWikiDoc();
        addProvenance();
        addAuthority();
        addDataCategory();
    }
}
