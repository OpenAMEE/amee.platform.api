package com.amee.platform.resource.dataitemvalue;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBuilder;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.data.DataItem;
import com.amee.service.item.DataItemValuesFilter;

/**
 * A resource to show the history of values for a {@link DataItem} for a specific {@link ItemValueDefinition}.
 */
public interface DataItemValueHistoryResource {

    /**
     * A {@link ResourceBuilder} implementation for this resource.
     */
    public static interface Builder extends ResourceBuilder {

        /**
         * Handle a request.
         *
         * @param requestWrapper the current {@link RequestWrapper}
         * @param filter         the {@link com.amee.service.item.DataItemValuesFilter} for this request
         */
        public void handle(RequestWrapper requestWrapper, DataItemValuesFilter filter);

        /**
         * Get a {@link DataItemValuesResource.Renderer} for this {@link ResourceBuilder}.
         * <p/>
         * Note: This borrows the renderer from DataItemValuesResource.
         *
         * @param requestWrapper the current {@link RequestWrapper}
         * @return a {@link DataItemValuesResource.Renderer}
         */
        public DataItemValuesResource.Renderer getRenderer(RequestWrapper requestWrapper);

        /**
         * Get a {@link DataItemValueResource.Builder} for this {@link ResourceBuilder}.
         * <p/>
         * Note: This borrows the renderer from DataItemValuesResource.
         *
         * @param requestWrapper the current {@link RequestWrapper}
         * @return a {@link DataItemValueResource.Builder}
         */
        public DataItemValueResource.Builder getDataItemValueBuilder(RequestWrapper requestWrapper);

        public DataItemValuesResource.DataItemValuesFilterValidator getValidator(RequestWrapper requestWrapper);
    }
}
