package com.amee.platform.resource.datacategory;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class DataCategoryAcceptor {

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    public DataCategoryResource.DataCategoryValidationHelper getValidationHelper(RequestWrapper requestWrapper) {
        DataCategoryResource.DataCategoryValidationHelper validationHelper =
                (DataCategoryResource.DataCategoryValidationHelper)
                        resourceBeanFinder.getValidationHelper(
                                DataCategoryResource.DataCategoryValidationHelper.class, requestWrapper);
        validationHelper.setValidator(
                (DataCategoryResource.DataCategoryValidator)
                        resourceBeanFinder.getValidator(
                                DataCategoryResource.DataCategoryValidator.class, requestWrapper));
        return validationHelper;
    }
}
