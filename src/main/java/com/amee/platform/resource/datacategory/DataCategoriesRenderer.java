package com.amee.platform.resource.datacategory;

import com.amee.base.resource.Renderer;

public interface DataCategoriesRenderer extends Renderer {

    public void newDataCategory();

    public void setTruncated(boolean truncated);

    public DataCategoryRenderer getDataCategoryRenderer();
}
