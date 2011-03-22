package com.amee.platform.resource.dataitemvalue.v_3_4;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.transaction.AMEETransaction;
import com.amee.base.validation.ValidationException;
import com.amee.domain.IDataItemService;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.data.BaseDataItemValue;
import com.amee.domain.item.data.DataItem;
import com.amee.domain.item.data.DataItemNumberValueHistory;
import com.amee.domain.item.data.DataItemTextValueHistory;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.dataitemvalue.DataItemValueHistoryResource;
import com.amee.platform.resource.dataitemvalue.DataItemValueResource;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.invalidation.InvalidationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.4.0")
public class DataItemValueHistoryFormAcceptor_3_4_0 implements DataItemValueHistoryResource.FormAcceptor {

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private InvalidationService invalidationService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private IDataItemService dataItemService;

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

        // Authorized for DataItem?
        resourceAuthorizationService.ensureAuthorizedForModify(
                requestWrapper.getAttributes().get("activeUserUid"), dataItem);

        // Handle the DataItem submission.
        BaseDataItemValue newDataItemValue;
        if (itemValueDefinition.isDouble()) {
            newDataItemValue = new DataItemNumberValueHistory(itemValueDefinition, dataItem);
        } else {
            newDataItemValue = new DataItemTextValueHistory(itemValueDefinition, dataItem);
        }
        return handle(requestWrapper, newDataItemValue);
    }

    protected Object handle(RequestWrapper requestWrapper, BaseDataItemValue dataItemValue) {

        // Create Validator.
        DataItemValueResource.DataItemValueValidator validator = getValidator(requestWrapper);
        validator.setObject(dataItemValue);
        validator.initialise();

        // Validate the BaseDataItemValue.
        if (validator.isValid(requestWrapper.getFormParameters())) {

            // BaseDataItemValue was valid, we'll allow it to persist and invalidate the DataCategory.
            dataItemService.persist(dataItemValue);
            invalidationService.add(dataItemValue.getDataItem().getDataCategory());

            // Mark the DataItem as modified.
            dataItemValue.getDataItem().onModify();

            // Respond with the URI of the new item value.
            return ResponseHelper.getOK(
                    requestWrapper,
                    "/" + requestWrapper.getVersion() +
                            "/categories/" + requestWrapper.getAttributes().get("categoryIdentifier") +
                            "/items/" + requestWrapper.getAttributes().get("itemIdentifier") +
                            "/values/" + requestWrapper.getAttributes().get("valuePath") +
                            "/" + dataItemValue.getUid());
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
