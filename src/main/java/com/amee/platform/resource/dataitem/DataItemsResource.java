package com.amee.platform.resource.dataitem;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceAcceptor;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRenderer;
import com.amee.base.validation.ValidationException;

public interface DataItemsResource {

    public static interface Builder extends ResourceBuilder {
    }

    public static interface Renderer extends ResourceRenderer {

        public void newDataItem(DataItemResource.Renderer renderer);

        public void setTruncated(boolean truncated);
    }

    public static interface FormAcceptor extends ResourceAcceptor {

        public Object handle(RequestWrapper requestWrapper) throws ValidationException;
    }
}
