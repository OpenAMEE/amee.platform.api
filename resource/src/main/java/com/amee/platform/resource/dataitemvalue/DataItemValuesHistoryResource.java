package com.amee.platform.resource.dataitemvalue;

import com.amee.base.resource.ResourceRenderer;

public interface DataItemValuesHistoryResource {

    interface Renderer extends ResourceRenderer {

        void newDataItemValue(DataItemValueHistoryResource.Renderer renderer);

        void setTruncated(boolean truncated);
    }
}
