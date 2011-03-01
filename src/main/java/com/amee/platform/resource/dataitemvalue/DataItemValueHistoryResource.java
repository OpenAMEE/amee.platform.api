package com.amee.platform.resource.dataitemvalue;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBuilder;

public interface DataItemValueHistoryResource {

    public static interface Builder extends ResourceBuilder {

        // Note that this borrows the renderer from DataItemValuesResource.
        public DataItemValuesResource.Renderer getRenderer(RequestWrapper requestWrapper);
    }
}
