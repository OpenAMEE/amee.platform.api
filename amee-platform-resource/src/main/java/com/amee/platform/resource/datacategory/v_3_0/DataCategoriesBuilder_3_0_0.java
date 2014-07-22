package com.amee.platform.resource.datacategory.v_3_0;

import com.amee.base.domain.ResultsWrapper;
import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.transaction.AMEETransaction;
import com.amee.base.validation.ValidationException;
import com.amee.domain.data.DataCategory;
import com.amee.platform.resource.datacategory.DataCategoriesResource;
import com.amee.platform.resource.datacategory.DataCategoryResource;
import com.amee.platform.search.DataCategoriesFilter;
import com.amee.platform.search.DataCategoriesFilterValidationHelper;
import com.amee.platform.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.0.0")
public class DataCategoriesBuilder_3_0_0 implements DataCategoriesResource.Builder {

    @Autowired
    private SearchService searchService;

    @Autowired
    private DataCategoriesFilterValidationHelper validationHelper;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    private DataCategoriesResource.Renderer dataCategoriesRenderer;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {
        DataCategoriesFilter filter = new DataCategoriesFilter();
        filter.setLoadMetadatas(
                requestWrapper.getMatrixParameters().containsKey("full") ||
                        requestWrapper.getMatrixParameters().containsKey("authority") ||
                        requestWrapper.getMatrixParameters().containsKey("history") ||
                        requestWrapper.getMatrixParameters().containsKey("wikiDoc") ||
                        requestWrapper.getMatrixParameters().containsKey("provenance"));
        filter.setLoadEntityTags(
                requestWrapper.getMatrixParameters().containsKey("full") ||
                        requestWrapper.getMatrixParameters().containsKey("tags"));
        validationHelper.setDataCategoryFilter(filter);
        if (validationHelper.isValid(requestWrapper.getQueryParameters())) {
            handle(requestWrapper, filter);
            DataCategoriesResource.Renderer renderer = getRenderer(requestWrapper);
            renderer.ok();
            return renderer.getObject();
        } else {
            throw new ValidationException(validationHelper.getValidationResult());
        }
    }

    protected void handle(
            RequestWrapper requestWrapper,
            DataCategoriesFilter filter) {
        // Setup Renderer.
        DataCategoriesResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.start();
        // Add Data Categories to Renderer and build.
        ResultsWrapper<DataCategory> resultsWrapper = searchService.getDataCategories(filter);
        renderer.setTruncated(resultsWrapper.isTruncated());
        DataCategoryResource.Builder dataCategoryBuilder = getDataCategoryBuilder(requestWrapper);
        for (DataCategory dataCategory : resultsWrapper.getResults()) {
            dataCategoryBuilder.handle(requestWrapper, dataCategory);
            renderer.newDataCategory(dataCategoryBuilder.getRenderer(requestWrapper));
        }
    }

    public DataCategoriesResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (dataCategoriesRenderer == null) {
            dataCategoriesRenderer = (DataCategoriesResource.Renderer) resourceBeanFinder.getRenderer(DataCategoriesResource.Renderer.class, requestWrapper);
        }
        return dataCategoriesRenderer;
    }

    private DataCategoryResource.Builder getDataCategoryBuilder(RequestWrapper requestWrapper) {
        return (DataCategoryResource.Builder)
                resourceBeanFinder.getBuilder(DataCategoryResource.Builder.class, requestWrapper);
    }
}