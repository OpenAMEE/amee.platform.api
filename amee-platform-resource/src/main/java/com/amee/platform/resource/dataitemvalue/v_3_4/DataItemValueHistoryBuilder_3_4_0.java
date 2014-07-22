package com.amee.platform.resource.dataitemvalue.v_3_4;

import com.amee.base.domain.ResultsWrapper;
import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.transaction.AMEETransaction;
import com.amee.base.validation.ValidationException;
import com.amee.domain.DataItemService;
import com.amee.domain.DataItemValuesFilter;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.data.BaseDataItemValue;
import com.amee.domain.item.data.DataItem;
import com.amee.platform.resource.dataitemvalue.DataItemValueResource;
import com.amee.platform.resource.dataitemvalue.DataItemValuesResource;
import com.amee.platform.resource.dataitemvalue.v_3_6.DataItemValueHistoryBuilder_3_6_0;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.4.0")
public class DataItemValueHistoryBuilder_3_4_0 extends DataItemValueHistoryBuilder_3_6_0 {

    private DataItemValuesResource.Renderer renderer;

    /**
     * Handle a request.
     * 
     * @param requestWrapper the current {@link RequestWrapper}
     */
    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {

        // Get resource entities for this request.
        DataCategory dataCategory = resourceService.getDataCategoryWhichHasItemDefinition(requestWrapper);
        DataItem dataItem = resourceService.getDataItem(requestWrapper, dataCategory);
        ItemValueDefinition itemValueDefinition = resourceService.getItemValueDefinition(requestWrapper, dataItem);

        // Authorized for DataItem?
        resourceAuthorizationService.ensureAuthorizedForBuild(
            requestWrapper.getAttributes().get("activeUserUid"), dataItem);

        // Create filter.
        DataItemValuesFilter filter = new DataItemValuesFilter();
        filter.setDataItem(dataItem);
        filter.setItemValueDefinition(itemValueDefinition);
        filter.setStartDate(DataItemService.EPOCH);

        // Create validator.
        DataItemValuesResource.FilterValidator validator = getValidator(requestWrapper);
        validator.setObject(filter);
        validator.setDefaultStartDate(DataItemService.EPOCH);
        validator.initialise();

        // Is the filter valid?
        if (validator.isValid(requestWrapper.getQueryParameters())) {
            // Handle the DataItem & ItemValueDefinition.
            handle(requestWrapper, filter);
            // Render the response.
            DataItemValuesResource.Renderer renderer = getRenderer(requestWrapper);
            renderer.ok();
            return renderer.getObject();
        } else {
            throw new ValidationException(validator.getValidationResult());
        }
    }

    /**
     * Handle a request. Typically called by the method above but could also be called by another resource
     * as part of build its own representation.
     * 
     * @param requestWrapper the current {@link RequestWrapper}
     * @param filter the {@link DataItemValuesFilter} for this request
     */
    @Override
    public void handle(RequestWrapper requestWrapper, DataItemValuesFilter filter) {

        // Get the item values.
        ResultsWrapper<BaseDataItemValue> resultsWrapper = dataItemService.getAllItemValues(filter);

        // Setup Renderer.
        DataItemValuesResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.start();
        renderer.setTruncated(resultsWrapper.isTruncated());

        // Add Data Item Values to Renderer and build.
        DataItemValueResource.Builder dataItemValueBuilder = getDataItemValueBuilder(requestWrapper);
        for (BaseDataItemValue itemValue : resultsWrapper.getResults()) {
            dataItemValueBuilder.handle(requestWrapper, itemValue);
            renderer.newDataItemValue(dataItemValueBuilder.getRenderer(requestWrapper));
        }
    }

    /**
     * Get a {@link DataItemValuesResource.Renderer} for this {@link com.amee.base.resource.ResourceBuilder}.
     * <p/>
     * Note: This borrows the renderer from DataItemValuesResource.
     * 
     * @param requestWrapper the current {@link RequestWrapper}
     * @return a {@link DataItemValuesResource.Renderer}
     */
    public DataItemValuesResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (renderer == null) {
            renderer = (DataItemValuesResource.Renderer) resourceBeanFinder.getRenderer(DataItemValuesResource.Renderer.class, requestWrapper);
        }
        return renderer;
    }

    /**
     * Get a {@link DataItemValueResource.Builder} for this {@link com.amee.base.resource.ResourceBuilder}.
     * <p/>
     * This borrows the renderer from DataItemValuesResource.
     * 
     * @param requestWrapper the current {@link RequestWrapper}
     * @return a {@link DataItemValueResource.Builder}
     */
    public DataItemValueResource.Builder getDataItemValueBuilder(RequestWrapper requestWrapper) {
        return (DataItemValueResource.Builder) resourceBeanFinder.getBuilder(DataItemValueResource.Builder.class, requestWrapper);
    }
}
