package com.amee.platform.resource.search;

import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRenderer;
import com.amee.platform.resource.ResourceValidator;
import com.amee.platform.resource.datacategory.DataCategoryResource;
import com.amee.platform.resource.dataitem.DataItemResource;
import com.amee.platform.search.SearchFilter;

public interface SearchResource {

    public static interface Builder extends ResourceBuilder {
    }

    public static interface Renderer extends ResourceRenderer {

        public void newDataCategory(DataCategoryResource.Renderer dataCategoryRenderer);

        public void newDataItem(DataItemResource.Renderer dataItemRenderer);

        public void setTruncated(boolean truncated);
    }

    public static interface SearchFilterValidationHelper extends ResourceValidator<SearchFilter> {

        // public SearchFilter getSearchFilter();

        // public void setSearchFilter(SearchFilter searchFilter);
    }
}
