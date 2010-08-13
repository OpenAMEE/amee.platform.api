package com.amee.platform.resource.dataitem;

import com.amee.base.resource.Renderer;

public interface DataItemsRenderer extends Renderer {

    public void newDataItem(DataItemRenderer dataItemRenderer);

    public void setTruncated(boolean truncated);
}
