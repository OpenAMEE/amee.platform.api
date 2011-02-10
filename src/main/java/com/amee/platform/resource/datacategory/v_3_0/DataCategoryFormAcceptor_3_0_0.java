package com.amee.platform.resource.datacategory.v_3_0;

import com.amee.base.domain.Since;
import com.amee.base.resource.MissingAttributeException;
import com.amee.base.resource.NotFoundException;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.transaction.AMEETransaction;
import com.amee.base.validation.ValidationException;
import com.amee.domain.data.DataCategory;
import com.amee.platform.resource.datacategory.DataCategoryAcceptor;
import com.amee.platform.resource.datacategory.DataCategoryResource;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.data.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.0.0")
public class DataCategoryFormAcceptor_3_0_0 extends DataCategoryAcceptor implements DataCategoryResource.FormAcceptor {

    @Autowired
    private DataService dataService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {ValidationException.class})
    public Object handle(RequestWrapper requestWrapper) throws ValidationException {
        // Get the DataCategory identifier.
        String dataCategoryIdentifier = requestWrapper.getAttributes().get("categoryIdentifier");
        if (dataCategoryIdentifier != null) {
            // Get DataCategory.
            DataCategory dataCategory = dataService.getDataCategoryByIdentifier(dataCategoryIdentifier);
            if (dataCategory != null) {
                // Authorized?
                resourceAuthorizationService.ensureAuthorizedForModify(
                        requestWrapper.getAttributes().get("activeUserUid"), dataCategory);
                // Handle the DataCategory update (entity updated via validation binding).
                DataCategoryResource.DataCategoryValidationHelper validationHelper = getValidationHelper(requestWrapper);
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