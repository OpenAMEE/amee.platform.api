package com.amee.platform.resource.datacategory;

import com.amee.base.domain.ResultsWrapper;
import com.amee.base.resource.RendererBeanFinder;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.validation.ValidationException;
import com.amee.domain.data.DataCategory;
import com.amee.platform.search.DataCategoryFilter;
import com.amee.platform.search.DataCategoryFilterValidationHelper;
import com.amee.platform.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
public class DataCategoriesBuilder implements ResourceBuilder {

    @Autowired
    private SearchService searchService;

    @Autowired
    private DataCategoryFilterValidationHelper validationHelper;

    @Autowired
    private DataCategoryBuilder dataCategoryBuilder;

    @Autowired
    private RendererBeanFinder rendererBeanFinder;

    private DataCategoriesRenderer dataCategoriesRenderer;

    @Transactional(readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {
        DataCategoryFilter filter = new DataCategoryFilter();
        filter.setLoadMetadatas(
                requestWrapper.getMatrixParameters().containsKey("full") ||
                        requestWrapper.getMatrixParameters().containsKey("authority") ||
                        requestWrapper.getMatrixParameters().containsKey("wikiDoc") ||
                        requestWrapper.getMatrixParameters().containsKey("provenance"));
        filter.setLoadEntityTags(
                requestWrapper.getMatrixParameters().containsKey("full") ||
                        requestWrapper.getMatrixParameters().containsKey("tags"));
        validationHelper.setDataCategoryFilter(filter);
        if (validationHelper.isValid(requestWrapper.getQueryParameters())) {
            handle(requestWrapper, filter);
            DataCategoriesRenderer renderer = getDataCategoriesRenderer(requestWrapper);
            renderer.ok();
            return renderer.getObject();
        } else {
            throw new ValidationException(validationHelper.getValidationResult());
        }
    }

    protected void handle(
            RequestWrapper requestWrapper,
            DataCategoryFilter filter) {

        DataCategoriesRenderer renderer = getDataCategoriesRenderer(requestWrapper);
        renderer.start();

        ResultsWrapper<DataCategory> resultsWrapper = searchService.getDataCategories(filter);
        renderer.setTruncated(resultsWrapper.isTruncated());
        for (DataCategory dataCategory : resultsWrapper.getResults()) {
            dataCategoryBuilder.handle(requestWrapper, dataCategory);
            renderer.newDataCategory(dataCategoryBuilder.getDataCategoryRenderer(requestWrapper));
        }
    }

    public DataCategoriesRenderer getDataCategoriesRenderer(RequestWrapper requestWrapper) {
        if (dataCategoriesRenderer == null) {
            dataCategoriesRenderer = (DataCategoriesRenderer) rendererBeanFinder.getRenderer(DataCategoriesRenderer.class, requestWrapper);
        }
        return dataCategoriesRenderer;
    }
}