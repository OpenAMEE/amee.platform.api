package com.amee.platform.resource.dataitemvalue.v_3_6;

import com.amee.base.domain.ResultsWrapper;
import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.transaction.AMEETransaction;
import com.amee.base.validation.ValidationException;
import com.amee.domain.DataItemService;
import com.amee.domain.DataItemValuesFilter;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.data.BaseDataItemValue;
import com.amee.domain.item.data.DataItem;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.dataitemvalue.DataItemValueHistoryResource;
import com.amee.platform.resource.dataitemvalue.DataItemValuesHistoryResource;
import com.amee.platform.resource.dataitemvalue.DataItemValuesResource;
import com.amee.service.auth.ResourceAuthorizationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>A resource to show the history of values for a {@link DataItem} for a specific {@link ItemValueDefinition}.<p/>
 * 
 * This resource borrows renderers and builders from {@link DataItemValuesResource} as the representations are very similar.
 */
@Service
@Scope("prototype")
@Since("3.6.0")
public class DataItemValueHistoryBuilder_3_6_0 implements DataItemValueHistoryResource.Builder {

    @Autowired
    protected DataItemService dataItemService;

    @Autowired
    protected ResourceService resourceService;

    @Autowired
    protected ResourceBeanFinder resourceBeanFinder;

    @Autowired
    protected ResourceAuthorizationService resourceAuthorizationService;

    private DataItemValuesHistoryResource.Renderer valuesRenderer;

    private DataItemValueHistoryResource.Renderer valueRenderer;

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
            DataItemValuesHistoryResource.Renderer valuesRenderer = getValuesRenderer(requestWrapper);
            valuesRenderer.ok();
            return valuesRenderer.getObject();
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

        // Setup Values Renderer.
        DataItemValuesHistoryResource.Renderer valuesRenderer = getValuesRenderer(requestWrapper);
        valuesRenderer.start();
        valuesRenderer.setTruncated(resultsWrapper.isTruncated());

        // Get Value Renderer
        DataItemValueHistoryResource.Renderer valueRenderer = getValueRenderer(requestWrapper);

        // Add Data Item Values to Renderer and build.
        for (BaseDataItemValue itemValue : resultsWrapper.getResults()) {
            handle(requestWrapper, itemValue);
            valuesRenderer.newDataItemValue(valueRenderer);
        }
    }

    @Override
    public void handle(RequestWrapper requestWrapper, BaseDataItemValue dataItemValue) {

        // Get the Renderer.
        DataItemValueHistoryResource.Renderer renderer = getValueRenderer(requestWrapper);
        renderer.start();

        // Collect rendering options from matrix params.
        boolean full = requestWrapper.getMatrixParameters().containsKey("full");
        boolean path = requestWrapper.getMatrixParameters().containsKey("path");
        boolean category = requestWrapper.getMatrixParameters().containsKey("category");
        boolean item = requestWrapper.getMatrixParameters().containsKey("item");
        boolean audit = requestWrapper.getMatrixParameters().containsKey("audit");
        boolean itemValueDefinition = requestWrapper.getMatrixParameters().containsKey("itemValueDefinition");

        // New DataItemValue & basic.
        renderer.newDataItemValue(dataItemValue);
        renderer.addBasic();

        // Optionals.
        if (path || full) {
            renderer.addPath();
        }
        if (category || full) {
            renderer.addDataCategory();
        }
        if (item || full) {
            renderer.addDataItem();
        }
        if (audit || full) {
            renderer.addAudit();
        }
        if ((itemValueDefinition || full) && (dataItemValue.getItemValueDefinition() != null)) {
            renderer.addItemValueDefinition(dataItemValue.getItemValueDefinition());
        }
    }

    /**
     * Get a {@link DataItemValuesHistoryResource.Renderer} for this {@link com.amee.base.resource.ResourceBuilder}.
     * 
     * @param requestWrapper the current {@link RequestWrapper}
     * @return a {@link DataItemValuesHistoryResource.Renderer}
     */
    @Override
    public DataItemValuesHistoryResource.Renderer getValuesRenderer(RequestWrapper requestWrapper) {
        if (valuesRenderer == null) {
            valuesRenderer = (DataItemValuesHistoryResource.Renderer) resourceBeanFinder.getRenderer(DataItemValuesHistoryResource.Renderer.class,
                requestWrapper);
        }
        return valuesRenderer;
    }

    /**
     * Get a {@link DataItemValueHistoryResource.Renderer} for this {@link ResourceBuilder}.
     * 
     * @param requestWrapper the current {@link RequestWrapper}
     * @return a {@link DataItemValueHistoryResource.Renderer}
     */
    @Override
    public DataItemValueHistoryResource.Renderer getValueRenderer(RequestWrapper requestWrapper) {
        if (valueRenderer == null) {
            valueRenderer = (DataItemValueHistoryResource.Renderer) resourceBeanFinder.getRenderer(DataItemValueHistoryResource.Renderer.class, requestWrapper);
        }
        return valueRenderer;
    }

    @Override
    public DataItemValuesResource.FilterValidator getValidator(RequestWrapper requestWrapper) {
        return (DataItemValuesResource.FilterValidator) resourceBeanFinder.getValidator(
            DataItemValuesResource.FilterValidator.class, requestWrapper);
    }
}
