package com.amee.platform.resource.datacategory;

import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRenderer;

public interface DataCategoriesResource {

    interface Builder extends ResourceBuilder {
    }

    interface Renderer extends ResourceRenderer {

        public void newDataCategory(DataCategoryResource.Renderer renderer);

        public void setTruncated(boolean truncated);
    }
}
