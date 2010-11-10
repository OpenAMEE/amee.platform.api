package com.amee.platform.resource.dataitem.v_3_0;

import com.amee.base.domain.ResultsWrapper;
import com.amee.base.domain.Since;
import com.amee.base.resource.MissingAttributeException;
import com.amee.base.resource.NotFoundException;
import com.amee.base.resource.RendererBeanFinder;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.validation.ValidationException;
import com.amee.domain.IAMEEEntity;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.DataItem;
import com.amee.platform.resource.dataitem.DataItemsResource;
import com.amee.platform.search.DataItemsFilter;
import com.amee.platform.search.DataItemsFilterValidationHelper;
import com.amee.platform.search.SearchService;
import com.amee.service.data.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.0.0")
public class DataItemsBuilder_3_0_0 implements DataItemsResource.Builder {

    @Autowired
    private DataService dataService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private DataItemBuilder_3_0_0 dataItemBuilder;

    @Autowired
    private DataItemsFilterValidationHelper validationHelper;

    @Autowired
    private RendererBeanFinder rendererBeanFinder;

    private DataItemsResource.Renderer dataItemsRenderer;

    @Transactional(readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {
        // Get DataCategory identifier.
        String dataCategoryIdentifier = requestWrapper.getAttributes().get("categoryIdentifier");
        if (dataCategoryIdentifier != null) {
            // Get DataCategory.
            DataCategory dataCategory = dataService.getDataCategoryByIdentifier(dataCategoryIdentifier);
            if ((dataCategory != null) && (dataCategory.getItemDefinition() != null)) {
                // Create filter and do search.
                DataItemsFilter filter = new DataItemsFilter(dataCategory.getItemDefinition());
                filter.setLoadDataItemValues(
                        requestWrapper.getMatrixParameters().containsKey("full") ||
                                requestWrapper.getMatrixParameters().containsKey("values"));
                filter.setLoadMetadatas(
                        requestWrapper.getMatrixParameters().containsKey("full") ||
                                requestWrapper.getMatrixParameters().containsKey("wikiDoc") ||
                                requestWrapper.getMatrixParameters().containsKey("provenance"));
                validationHelper.setDataItemFilter(filter);
                if (validationHelper.isValid(requestWrapper.getQueryParameters())) {
                    handle(requestWrapper, dataCategory, filter);
                    DataItemsResource.Renderer renderer = getRenderer(requestWrapper);
                    renderer.ok();
                    return renderer.getObject();
                } else {
                    throw new ValidationException(validationHelper.getValidationResult());
                }
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new MissingAttributeException("categoryIdentifier");
        }
    }

    protected void handle(
            RequestWrapper requestWrapper,
            DataCategory dataCategory,
            DataItemsFilter filter) {
        // Setup Renderer.
        DataItemsResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.start();
        // Add Data Items to Renderer.
        ResultsWrapper<IAMEEEntity> resultsWrapper = searchService.getDataItems(dataCategory, filter);
        renderer.setTruncated(resultsWrapper.isTruncated());
        for (IAMEEEntity entity : resultsWrapper.getResults()) {
            dataItemBuilder.handle(requestWrapper, (DataItem) entity);
            renderer.newDataItem(dataItemBuilder.getRenderer(requestWrapper));
        }
    }

    public DataItemsResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (dataItemsRenderer == null) {
            dataItemsRenderer = (DataItemsResource.Renderer) rendererBeanFinder.getRenderer(DataItemsResource.Renderer.class, requestWrapper);
        }
        return dataItemsRenderer;
    }
}    
