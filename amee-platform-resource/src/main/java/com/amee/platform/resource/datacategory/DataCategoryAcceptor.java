package com.amee.platform.resource.datacategory;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class DataCategoryAcceptor {

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    public DataCategoryResource.DataCategoryValidator getValidator(RequestWrapper requestWrapper) {
        return (DataCategoryResource.DataCategoryValidator)
                resourceBeanFinder.getBaseValidator(
                        DataCategoryResource.DataCategoryValidator.class, requestWrapper);
    }
}
