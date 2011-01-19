package com.amee.platform.resource.datacategory;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceAcceptor;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRenderer;

public interface DataCategoriesResource {

    interface Builder extends ResourceBuilder {
    }

    interface Renderer extends ResourceRenderer {

        public void newDataCategory(DataCategoryResource.Renderer renderer);

        public void setTruncated(boolean truncated);
    }

    public static interface FormAcceptor extends ResourceAcceptor {
        public DataCategoryResource.DataCategoryValidationHelper getValidationHelper(RequestWrapper requestWrapper);
    }
}
