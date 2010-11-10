package com.amee.platform.resource.dataitem;

import com.amee.base.resource.ResourceRenderer;
import com.amee.base.resource.ResourceBuilder;

public interface DataItemsResource {

    interface Builder extends ResourceBuilder {
    }

    interface Renderer extends ResourceRenderer {

        public void newDataItem(DataItemResource.Renderer renderer);

        public void setTruncated(boolean truncated);
    }
}
