package com.amee.platform.resource.search;

import com.amee.base.resource.Renderer;
import com.amee.platform.resource.datacategory.DataCategoryRenderer;
import com.amee.platform.resource.dataitem.DataItemRenderer;

public interface SearchRenderer extends Renderer {

    public void newDataCategory(DataCategoryRenderer dataCategoryRenderer);

    public void newDataItem(DataItemRenderer dataItemRenderer);

    public void setTruncated(boolean truncated);

    public Object getObject();
}
