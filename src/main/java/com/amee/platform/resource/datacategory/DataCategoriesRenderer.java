package com.amee.platform.resource.datacategory;

import com.amee.base.resource.Renderer;

public interface DataCategoriesRenderer extends Renderer {

    public void newDataCategory(DataCategoryRenderer dataCategoryRenderer);

    public void setTruncated(boolean truncated);
}
