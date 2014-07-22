package com.amee.platform.resource.dataitemvalue.v_3_4;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.transaction.AMEETransaction;
import com.amee.base.validation.ValidationException;
import com.amee.domain.DataItemService;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.data.BaseDataItemValue;
import com.amee.domain.item.data.DataItem;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.dataitemvalue.DataItemValueResource;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.invalidation.InvalidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.4.0")
public class DataItemValueFormAcceptor_3_4_0 implements DataItemValueResource.FormAcceptor {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private InvalidationService invalidationService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Object handle(RequestWrapper requestWrapper) throws ValidationException {

        // Get resource entities for this request.
        DataCategory dataCategory = resourceService.getDataCategoryWhichHasItemDefinition(requestWrapper);
        DataItem dataItem = resourceService.getDataItem(requestWrapper, dataCategory);
        ItemValueDefinition itemValueDefinition = resourceService.getItemValueDefinition(requestWrapper, dataItem);
        BaseDataItemValue dataItemValue = resourceService.getDataItemValue(requestWrapper, dataItem, itemValueDefinition);

        // Authorized for DataItem?
        resourceAuthorizationService.ensureAuthorizedForModify(
                requestWrapper.getAttributes().get("activeUserUid"), dataItem);

        // Handle the DataItem submission.
        return handle(requestWrapper, dataItemValue);
    }

    @Override
    public Object handle(RequestWrapper requestWrapper, BaseDataItemValue dataItemValue) {

        // Create Validator.
        DataItemValueResource.DataItemValueValidator validator = getValidator(requestWrapper);
        validator.setObject(dataItemValue);
        validator.initialise();

        // Is the Data Item Value valid?
        if (validator.isValid(requestWrapper.getFormParameters())) {
            // BaseDataItemValue was valid, we'll allow it to persist and invalidate the DataCategory.
            invalidationService.add(dataItemValue.getDataItem().getDataCategory());
            // Mark the DataItem as modified.
            dataItemValue.getDataItem().onModify();
            return ResponseHelper.getOK(requestWrapper, null, dataItemValue.getUid());
        } else {
            throw new ValidationException(validator.getValidationResult());
        }
    }

    protected DataItemValueResource.DataItemValueValidator getValidator(RequestWrapper requestWrapper) {
        return (DataItemValueResource.DataItemValueValidator)
                resourceBeanFinder.getValidator(
                        DataItemValueResource.DataItemValueValidator.class, requestWrapper);
    }
}
