package com.amee.platform.resource.search.v_3_0;

import com.amee.base.domain.ResultsWrapper;
import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.validation.ValidationException;
import com.amee.domain.IAMEEEntity;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.DataItem;
import com.amee.platform.resource.datacategory.DataCategoryResource;
import com.amee.platform.resource.dataitem.DataItemResource;
import com.amee.platform.resource.search.SearchResource;
import com.amee.platform.search.SearchFilter;
import com.amee.platform.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.0.0")
public class SearchBuilder_3_0_0 implements SearchResource.Builder {

    @Autowired
    private SearchService searchService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    private SearchResource.Renderer searchRenderer;

    @Transactional(readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {
        SearchFilter filter = new SearchFilter();
        filter.setLoadDataItemValues(
                requestWrapper.getMatrixParameters().containsKey("full") ||
                        requestWrapper.getMatrixParameters().containsKey("values"));
        filter.setLoadMetadatas(
                requestWrapper.getMatrixParameters().containsKey("full") ||
                        requestWrapper.getMatrixParameters().containsKey("authority") ||
                        requestWrapper.getMatrixParameters().containsKey("wikiDoc") ||
                        requestWrapper.getMatrixParameters().containsKey("provenance"));
        SearchResource.SearchFilterValidationHelper validationHelper = getValidationHelper(requestWrapper);
        validationHelper.setSearchFilter(filter);
        if (validationHelper.isValid(requestWrapper.getQueryParameters())) {
            handle(requestWrapper, filter);
            SearchResource.Renderer renderer = getRenderer(requestWrapper);
            renderer.ok();
            return renderer.getObject();
        } else {
            throw new ValidationException(validationHelper.getValidationResult());
        }
    }

    protected void handle(
            RequestWrapper requestWrapper,
            SearchFilter filter) {
        SearchResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.start();
        ResultsWrapper<IAMEEEntity> resultsWrapper = searchService.getEntities(filter);
        renderer.setTruncated(resultsWrapper.isTruncated());
        DataCategoryResource.Builder dataCategoryBuilder = getDataCategoryBuilder(requestWrapper);
        DataItemResource.Builder dataItemBuilder = getDataItemBuilder(requestWrapper);
        for (IAMEEEntity entity : resultsWrapper.getResults()) {
            switch (entity.getObjectType()) {
                case DC:
                    dataCategoryBuilder.handle(requestWrapper, (DataCategory) entity);
                    renderer.newDataCategory(dataCategoryBuilder.getRenderer(requestWrapper));
                    break;
                case DI:
                case NDI:
                    dataItemBuilder.handle(requestWrapper, (DataItem) entity);
                    renderer.newDataItem(dataItemBuilder.getRenderer(requestWrapper));
                    break;
            }
        }
    }

    public SearchResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (searchRenderer == null) {
            searchRenderer = (SearchResource.Renderer) resourceBeanFinder.getRenderer(SearchResource.Renderer.class, requestWrapper);
        }
        return searchRenderer;
    }

    private DataCategoryResource.Builder getDataCategoryBuilder(RequestWrapper requestWrapper) {
        return (DataCategoryResource.Builder)
                resourceBeanFinder.getBuilder(DataCategoryResource.Builder.class, requestWrapper);
    }

    private DataItemResource.Builder getDataItemBuilder(RequestWrapper requestWrapper) {
        return (DataItemResource.Builder)
                resourceBeanFinder.getBuilder(DataItemResource.Builder.class, requestWrapper);
    }

    private SearchResource.SearchFilterValidationHelper getValidationHelper(RequestWrapper requestWrapper) {
        return (SearchResource.SearchFilterValidationHelper)
                resourceBeanFinder.getValidationHelper(SearchResource.SearchFilterValidationHelper.class, requestWrapper);
    }
}