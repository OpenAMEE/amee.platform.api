package com.amee.platform.resource.search;

import com.amee.base.resource.ResourceRenderer;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ValidationResult;
import com.amee.platform.resource.datacategory.DataCategoryResource;
import com.amee.platform.resource.dataitem.DataItemResource;
import com.amee.platform.search.SearchFilter;

import java.util.Map;

public class SearchResource {

    public interface Builder extends ResourceBuilder {
    }

    public static interface Renderer extends ResourceRenderer {

        public void newDataCategory(DataCategoryResource.Renderer dataCategoryRenderer);

        public void newDataItem(DataItemResource.Renderer dataItemRenderer);

        public void setTruncated(boolean truncated);

        public Object getObject();
    }

    public static interface SearchFilterValidationHelper {

        public SearchFilter getSearchFilter();

        public void setSearchFilter(SearchFilter searchFilter);

        public boolean isValid(Map<String, String> queryParameters);

        public ValidationResult getValidationResult();
    }
}
