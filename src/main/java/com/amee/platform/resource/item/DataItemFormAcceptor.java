package com.amee.platform.resource.item;

import com.amee.base.resource.*;
import com.amee.base.validation.ValidationException;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.DataItem;
import com.amee.service.data.DataService;
import com.amee.service.environment.EnvironmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
public class DataItemFormAcceptor implements ResourceAcceptor {

    @Autowired
    private EnvironmentService environmentService;

    @Autowired
    private DataService dataService;

    @Autowired
    private DataItemValidationHelper validationHelper;

    @Transactional(rollbackFor = {ValidationException.class})
    public Object handle(RequestWrapper requestWrapper) throws ValidationException {
        // Get DataCategory identifier.
        String dataCategoryIdentifier = requestWrapper.getAttributes().get("categoryIdentifier");
        if (dataCategoryIdentifier != null) {
            // Get DataCategory.
            DataCategory dataCategory = dataService.getDataCategoryByIdentifier(
                    environmentService.getEnvironmentByName("AMEE"), dataCategoryIdentifier);
            if (dataCategory != null) {
                // Get DataItem identifier.
                String dataItemIdentifier = requestWrapper.getAttributes().get("itemIdentifier");
                if (dataItemIdentifier != null) {
                    // Get DataItem.
                    DataItem dataItem = dataService.getDataItemByUid(dataCategory, dataItemIdentifier);
                    if (dataItem != null) {
                        validationHelper.setDataItem(dataItem);
                        if (validationHelper.isValid(requestWrapper.getFormParameters())) {
                            return ResponseHelper.getOK(requestWrapper);
                        } else {
                            throw new ValidationException(validationHelper.getValidationResult());
                        }
                    } else {
                        throw new NotFoundException();
                    }
                } else {
                    throw new MissingAttributeException("itemIdentifier");
                }
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new MissingAttributeException("categoryIdentifier");
        }
    }
}