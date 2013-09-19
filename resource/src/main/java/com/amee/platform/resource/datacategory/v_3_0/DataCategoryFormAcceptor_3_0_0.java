package com.amee.platform.resource.datacategory.v_3_0;

import com.amee.base.domain.Since;
import com.amee.base.resource.NotAuthorizedException;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.transaction.AMEETransaction;
import com.amee.base.validation.ValidationException;
import com.amee.domain.data.DataCategory;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.datacategory.DataCategoryAcceptor;
import com.amee.platform.resource.datacategory.DataCategoryResource;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.invalidation.InvalidationService;
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
    private InvalidationService invalidationService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private ResourceService resourceService;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Object handle(RequestWrapper requestWrapper) throws ValidationException {

        // Get DataCategory.
        DataCategory dataCategory = resourceService.getDataCategory(requestWrapper);

        // Authorized?
        resourceAuthorizationService.ensureAuthorizedForModify(
                requestWrapper.getAttributes().get("activeUserUid"), dataCategory);

        // Nobody is allowed to modify the root DataCategory.
        checkDataCategoryIsNotRoot(dataCategory);

        // Handle the DataCategory update (entity updated via validation binding).
        DataCategoryResource.DataCategoryValidator validator = getValidator(requestWrapper);
        validator.setObject(dataCategory);
        validator.initialise();
        if (validator.isValid(requestWrapper.getFormParameters())) {
            invalidationService.add(dataCategory);
            return ResponseHelper.getOK(requestWrapper, null, dataCategory.getUid());
        } else {
            throw new ValidationException(validator.getValidationResult());
        }
    }

    /**
     * Check that the {@link DataCategory} is not the root (has an empty path).
     *
     * @param dataCategory to check
     */
    private void checkDataCategoryIsNotRoot(DataCategory dataCategory) {
        if (dataCategory.getPath().isEmpty()) {
            throw new NotAuthorizedException("Not authorized to modify root DataCategory.");
        }
    }
}