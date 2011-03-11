package com.amee.platform.resource.dataitem.v_3_0;

import com.amee.base.domain.ResultsWrapper;
import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.transaction.AMEETransaction;
import com.amee.base.validation.ValidationException;
import com.amee.domain.data.DataCategory;
import com.amee.domain.item.data.DataItem;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.dataitem.DataItemResource;
import com.amee.platform.resource.dataitem.DataItemsResource;
import com.amee.platform.search.DataItemsFilter;
import com.amee.platform.search.DataItemsFilterValidationHelper;
import com.amee.platform.search.SearchService;
import com.amee.service.auth.ResourceAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.0.0")
public class DataItemsBuilder_3_0_0 implements DataItemsResource.Builder {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private DataItemsFilterValidationHelper validationHelper;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    private DataItemsResource.Renderer dataItemsRenderer;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {

        // Get entities.
        DataCategory dataCategory = resourceService.getDataCategoryWhichHasItemDefinition(requestWrapper);

        // Authorized?
        resourceAuthorizationService.ensureAuthorizedForBuild(
                requestWrapper.getAttributes().get("activeUserUid"), dataCategory);

        // Create filter.
        DataItemsFilter filter = new DataItemsFilter(dataCategory.getItemDefinition());
        filter.setLoadDataItemValues(
                requestWrapper.getMatrixParameters().containsKey("full") ||
                        requestWrapper.getMatrixParameters().containsKey("values"));
        filter.setLoadMetadatas(
                requestWrapper.getMatrixParameters().containsKey("full") ||
                        requestWrapper.getMatrixParameters().containsKey("wikiDoc") ||
                        requestWrapper.getMatrixParameters().containsKey("provenance"));
        validationHelper.setDataItemFilter(filter);

        // Do validation.
        if (validationHelper.isValid(requestWrapper.getQueryParameters())) {
            handle(requestWrapper, dataCategory, filter);
            DataItemsResource.Renderer renderer = getRenderer(requestWrapper);
            renderer.ok();
            return renderer.getObject();
        } else {
            throw new ValidationException(validationHelper.getValidationResult());
        }
    }

    protected void handle(
            RequestWrapper requestWrapper,
            DataCategory dataCategory,
            DataItemsFilter filter) {

        // Setup Renderer.
        DataItemsResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.start();

        // Add Data Items to Renderer and build.
        ResultsWrapper<DataItem> resultsWrapper = searchService.getDataItems(dataCategory, filter);
        renderer.setTruncated(resultsWrapper.isTruncated());
        DataItemResource.Builder dataItemBuilder = getDataItemBuilder(requestWrapper);
        for (DataItem dataItem : resultsWrapper.getResults()) {
            dataItemBuilder.handle(requestWrapper, dataItem);
            renderer.newDataItem(dataItemBuilder.getRenderer(requestWrapper));
        }
    }

    public DataItemsResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (dataItemsRenderer == null) {
            dataItemsRenderer = (DataItemsResource.Renderer) resourceBeanFinder.getRenderer(DataItemsResource.Renderer.class, requestWrapper);
        }
        return dataItemsRenderer;
    }

    private DataItemResource.Builder getDataItemBuilder(RequestWrapper requestWrapper) {
        return (DataItemResource.Builder)
                resourceBeanFinder.getBuilder(DataItemResource.Builder.class, requestWrapper);
    }
}
