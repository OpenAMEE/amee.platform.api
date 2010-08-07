package com.amee.platform.resource.search;

import com.amee.base.resource.Renderer;
import com.amee.platform.resource.datacategory.DataCategoryRenderer;
import com.amee.platform.resource.dataitem.DataItemRenderer;

public interface SearchRenderer extends Renderer {

    public void newDataCategory();

    public void newDataItem();

    public void setTruncated(boolean truncated);

    public DataCategoryRenderer getDataCategoryRenderer();

    public DataItemRenderer getDataItemRenderer();

    public Object getObject();
}
