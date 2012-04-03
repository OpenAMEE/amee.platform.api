package com.amee.platform.resource.dataitemvalue;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceAcceptor;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRenderer;
import com.amee.base.validation.ValidationException;
import com.amee.domain.DataItemValuesFilter;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.data.BaseDataItemValue;
import com.amee.domain.item.data.DataItem;

/**
 * A resource to show the history of values for a {@link DataItem} for a specific {@link ItemValueDefinition}.
 */
public interface DataItemValueHistoryResource {

    /**
     * A {@link ResourceBuilder} implementation for this resource.
     */
    interface Builder extends ResourceBuilder {

        /**
         * Handle a request.
         * 
         * @param requestWrapper the current {@link RequestWrapper}
         * @param filter the {@link com.amee.domain.DataItemValuesFilter} for this request
         */
        void handle(RequestWrapper requestWrapper, DataItemValuesFilter filter);

        /**
         * Handle a request
         * 
         * @param requestWrapper the current {@link RequestWrapper}
         * @param dataItemValue the data item value history being rendererd
         */
        void handle(RequestWrapper requestWrapper, BaseDataItemValue dataItemValue);

        /**
         * Get a {@link DataItemValuesHistoryResource.Renderer} for this {@link ResourceBuilder}.
         * 
         * @param requestWrapper the current {@link RequestWrapper}
         * @return a {@link DataItemValuesResource.Renderer}
         */
        DataItemValuesHistoryResource.Renderer getValuesRenderer(RequestWrapper requestWrapper);

        /**
         * Get a {@link DataItemValueHistoryResource.Renderer} for this {@link ResourceBuilder}
         * 
         * @param requestWrapper
         * @return
         */
        DataItemValueHistoryResource.Renderer getValueRenderer(RequestWrapper requestWrapper);

        /**
         * Get a {@link com.amee.platform.resource.dataitemvalue.DataItemValuesResource.FilterValidator} for this {@link ResourceBuilder}.
         * <p/>
         * Note: This borrows the validator from {@link DataItemValuesResource}.
         * 
         * @param requestWrapper the current {@link RequestWrapper}
         * @return a {@link com.amee.platform.resource.dataitemvalue.DataItemValuesResource.FilterValidator}
         */
        DataItemValuesResource.FilterValidator getValidator(RequestWrapper requestWrapper);
    }

    /**
     * A {@link ResourceAcceptor} implementation for this resource. Handles POSTing item values to a {@link DataItem}.
     */
    interface FormAcceptor extends ResourceAcceptor {

        /**
         * Handle POSTing of an item value to a {@link DataItem} history.
         * 
         * @param requestWrapper the current {@link RequestWrapper}
         * @return the response object
         * @throws ValidationException encapsulates {@link com.amee.base.resource.ValidationResult}s
         */
        @Override
        Object handle(RequestWrapper requestWrapper) throws ValidationException;
    }

    interface Renderer extends ResourceRenderer {

        void newDataItemValue(BaseDataItemValue dataItemValue);

        void addBasic();

        void addPath();

        void addDataCategory();

        void addDataItem();

        void addAudit();

        void addItemValueDefinition(ItemValueDefinition itemValueDefinition);
    }
}
