package com.amee.platform.resource.dataitem.v_3_4;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.transaction.AMEETransaction;
import com.amee.base.validation.ValidationException;
import com.amee.domain.DataItemService;
import com.amee.domain.data.DataCategory;
import com.amee.domain.item.data.DataItem;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.dataitem.DataItemResource;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.invalidation.InvalidationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Update a DataItem from a 'form' submission.
 */
@Service
@Scope("prototype")
@Since("3.4.0")
public class DataItemFormAcceptor_3_4_0 implements DataItemResource.FormAcceptor {

    @Autowired
    private InvalidationService invalidationService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private DataItemService dataItemService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Object handle(RequestWrapper requestWrapper) throws ValidationException {

        // Get entities.
        DataCategory dataCategory = resourceService.getDataCategoryWhichHasItemDefinition(requestWrapper);
        DataItem dataItem = resourceService.getDataItem(requestWrapper, dataCategory);

        // Authorized?
        resourceAuthorizationService.ensureAuthorizedForModify(
                requestWrapper.getAttributes().get("activeUserUid"), dataItem);

        // Handle the DataItem update.
        return handle(requestWrapper, dataItem);
    }

    protected Object handle(RequestWrapper requestWrapper, DataItem dataItem) {

        // Create validator.
        DataItemResource.DataItemValidator validator = getValidator(requestWrapper);
        validator.setObject(dataItem);
        validator.initialise();

        // Is the DataItem valid?
        if (validator.isValid(requestWrapper.getFormParameters())) {
            // DataItem was valid, we'll allow it to persist and invalidate the DataCategory.
            updateDataItemValues(dataItem);
            invalidationService.add(dataItem.getDataCategory());
            return ResponseHelper.getOK(requestWrapper, null, dataItem.getUid());
        } else {
            throw new ValidationException(validator.getValidationResult());
        }
    }

    /**
     * Update the DataItem Values for the supplied Data Item based on the values bean within the DataItem.
     * <p/>
     * Support for updating DataItem Values is since version 3.4.0.
     * 
     * @param dataItem
     *            to update
     */
    protected void updateDataItemValues(DataItem dataItem) {
        dataItemService.updateDataItemValues(dataItem);
    }

    protected DataItemResource.DataItemValidator getValidator(RequestWrapper requestWrapper) {
        return (DataItemResource.DataItemValidator) resourceBeanFinder.getBaseValidator(
                DataItemResource.DataItemValidator.class, requestWrapper);
    }
}