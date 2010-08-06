package com.amee.platform.resource.datacategory;

import com.amee.base.resource.*;
import com.amee.base.validation.ValidationException;
import com.amee.domain.data.DataCategory;
import com.amee.service.data.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
public class DataCategoryFormAcceptor implements ResourceAcceptor {

    @Autowired
    private DataService dataService;

    @Autowired
    private DataCategoryValidationHelper validationHelper;

    @Transactional(rollbackFor = {ValidationException.class})
    public Object handle(RequestWrapper requestWrapper) throws ValidationException {
        String categoryIdentifier = requestWrapper.getAttributes().get("categoryIdentifier");
        if (categoryIdentifier != null) {
            DataCategory dataCategory = dataService.getDataCategoryByUid(categoryIdentifier);
            if (dataCategory != null) {
                validationHelper.setDataCategory(dataCategory);
                if (validationHelper.isValid(requestWrapper.getFormParameters())) {
                    dataService.invalidate(dataCategory);
                    return ResponseHelper.getOK(requestWrapper);
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
}