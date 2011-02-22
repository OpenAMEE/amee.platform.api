package com.amee.platform.resource.dataitem.v_3_4;

import com.amee.base.domain.Since;
import com.amee.base.resource.*;
import com.amee.base.transaction.AMEETransaction;
import com.amee.base.validation.ValidationException;
import com.amee.domain.data.DataCategory;
import com.amee.domain.item.data.DataItem;
import com.amee.platform.resource.dataitem.DataItemResource;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.data.DataService;
import com.amee.service.invalidation.InvalidationService;
import com.amee.service.item.DataItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.4.0")
public class DataItemFormAcceptor_3_4_0 implements DataItemResource.FormAcceptor {

    @Autowired
    private InvalidationService invalidationService;

    @Autowired
    private DataService dataService;

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
        // Get DataCategory identifier.
        String dataCategoryIdentifier = requestWrapper.getAttributes().get("categoryIdentifier");
        if (dataCategoryIdentifier != null) {
            // Get DataCategory.
            DataCategory dataCategory = dataService.getDataCategoryByIdentifier(dataCategoryIdentifier);
            if (dataCategory != null) {
                // Get DataItem identifier.
                String dataItemIdentifier = requestWrapper.getAttributes().get("itemIdentifier");
                if (dataItemIdentifier != null) {
                    // Get DataItem.
                    DataItem dataItem = dataItemService.getDataItemByIdentifier(dataCategory, dataItemIdentifier);
                    if (dataItem != null) {
                        // Authorized?
                        resourceAuthorizationService.ensureAuthorizedForModify(
                                requestWrapper.getAttributes().get("activeUserUid"), dataItem);
                        // Handle the DataItem update.
                        return handle(requestWrapper, dataItem);
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

    protected Object handle(RequestWrapper requestWrapper, DataItem dataItem) {
        DataItemResource.DataItemValidator validator = getValidator(requestWrapper);
        validator.setObject(dataItem);
        validator.init();
        if (validator.isValid(requestWrapper.getFormParameters())) {
            // DataItem was valid, we'll allow it to persist and invalidate the DataCategory.
            updateDataItemValues(dataItem);
            invalidationService.add(dataItem.getDataCategory());
            return ResponseHelper.getOK(requestWrapper);
        } else {
            throw new ValidationException(validator.getValidationResult());
        }
    }

    /**
     * Update the Data Item Values for the supplied Data Item based on the values bean within the DataItem.
     *
     * @param dataItem to update
     */
    protected void updateDataItemValues(DataItem dataItem) {
        dataItemService.updateDataItemValues(dataItem);
    }

    protected DataItemResource.DataItemValidator getValidator(RequestWrapper requestWrapper) {
        return (DataItemResource.DataItemValidator)
                resourceBeanFinder.getValidationHelper(
                        DataItemResource.DataItemValidator.class, requestWrapper);
    }
}