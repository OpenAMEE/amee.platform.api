package com.amee.platform.resource.dataitem.v_3_4;

import com.amee.base.domain.Since;
import com.amee.base.resource.*;
import com.amee.base.transaction.AMEETransaction;
import com.amee.base.validation.ValidationException;
import com.amee.domain.data.DataCategory;
import com.amee.domain.item.data.DataItem;
import com.amee.platform.resource.dataitem.DataItemResource;
import com.amee.platform.resource.dataitem.DataItemsResource;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.data.DataService;
import com.amee.service.invalidation.InvalidationService;
import com.amee.service.item.DataItemService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.4.0")
public class DataItemsFormAcceptor_3_4_0 implements DataItemsResource.FormAcceptor {

    private final Log log = LogFactory.getLog(getClass());

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

    @Autowired
    private MessageSource messageSource;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Object handle(RequestWrapper requestWrapper) throws ValidationException {
        // Get DataCategory identifier.
        String categoryIdentifier = requestWrapper.getAttributes().get("categoryIdentifier");
        if (categoryIdentifier != null) {
            // Get DataCategory.
            DataCategory dataCategory = dataService.getDataCategoryByIdentifier(categoryIdentifier);
            if (dataCategory != null) {
                // Authorized?
                resourceAuthorizationService.ensureAuthorizedForModify(
                        requestWrapper.getAttributes().get("activeUserUid"), dataCategory);
                // DataCategory must have an ItemDefinition.
                if (dataCategory.isItemDefinitionPresent()) {
                    // Handle the DataItem submission.
                    DataItem dataItem = new DataItem(dataCategory, dataCategory.getItemDefinition());
                    dataItemService.persist(dataItem);
                    return handle(requestWrapper, dataItem);
                } else {
                    // Validation failure as there is no ItemDefinition.
                    throw new ValidationException(new ValidationResult(messageSource, "dataItem", "noItemDefinition"));
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
        validator.initialise();
        if (validator.isValid(requestWrapper.getFormParameters())) {
            // DataItem was valid, we'll allow it to persist and invalidate the DataCategory.
            dataItemService.updateDataItemValues(dataItem);
            invalidationService.add(dataItem.getDataCategory());
            return ResponseHelper.getOK(
                    requestWrapper,
                    "/" + requestWrapper.getVersion() +
                            "/categories/" + requestWrapper.getAttributes().get("categoryIdentifier") +
                            "/items/" + dataItem.getUid());
        } else {
            throw new ValidationException(validator.getValidationResult());
        }
    }

    protected DataItemResource.DataItemValidator getValidator(RequestWrapper requestWrapper) {
        return (DataItemResource.DataItemValidator)
                resourceBeanFinder.getValidator(
                        DataItemResource.DataItemValidator.class, requestWrapper);
    }
}
