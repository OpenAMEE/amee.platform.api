package com.amee.platform.resource.dataitem.v_3_4;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.transaction.AMEETransaction;
import com.amee.base.validation.ValidationException;
import com.amee.domain.DataItemValuesFilter;
import com.amee.domain.IDataItemService;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.item.BaseItemValue;
import com.amee.domain.item.data.DataItem;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.dataitem.DataItemResource;
import com.amee.platform.resource.dataitemvalue.DataItemValuesResource;
import com.amee.service.auth.ResourceAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Scope("prototype")
@Since("3.4.0")
public class DataItemBuilder_3_4_0 implements DataItemResource.Builder {

    @Autowired
    private IDataItemService dataItemService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    private DataItemResource.Renderer renderer;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {

        // Get resource entities for this request.
        DataCategory dataCategory = resourceService.getDataCategoryWhichHasItemDefinition(requestWrapper);
        DataItem dataItem = resourceService.getDataItem(requestWrapper, dataCategory);

        // Authorized for DataItem?
        resourceAuthorizationService.ensureAuthorizedForBuild(
                requestWrapper.getAttributes().get("activeUserUid"), dataItem);

        // Create filter.
        DataItemValuesFilter filter = new DataItemValuesFilter();
        filter.setStartDate(new Date());

        // Create validator.
        DataItemValuesResource.DataItemValuesFilterValidator validator = getValidator(requestWrapper);
        validator.setObject(filter);
        validator.setDefaultStartDate(new Date());
        validator.initialise();

        // Is the filter valid?
        if (validator.isValid(requestWrapper.getQueryParameters())) {
            // Update DataItem effective startDate.
            dataItem.setEffectiveStartDate(filter.getStartDate());
            // Handle the DataItem.
            handle(requestWrapper, dataItem);
            DataItemResource.Renderer renderer = getRenderer(requestWrapper);
            renderer.ok();
            return renderer.getObject();
        } else {
            throw new ValidationException(validator.getValidationResult());
        }
    }

    public void handle(RequestWrapper requestWrapper, DataItem dataItem) {

        DataItemResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.start();

        boolean full = requestWrapper.getMatrixParameters().containsKey("full");
        boolean name = requestWrapper.getMatrixParameters().containsKey("name");
        boolean label = requestWrapper.getMatrixParameters().containsKey("label");
        boolean path = requestWrapper.getMatrixParameters().containsKey("path");
        boolean parent = requestWrapper.getMatrixParameters().containsKey("parent");
        boolean audit = requestWrapper.getMatrixParameters().containsKey("audit");
        boolean wikiDoc = requestWrapper.getMatrixParameters().containsKey("wikiDoc");
        boolean provenance = requestWrapper.getMatrixParameters().containsKey("provenance");
        boolean itemDefinition = requestWrapper.getMatrixParameters().containsKey("itemDefinition");
        boolean values = requestWrapper.getMatrixParameters().containsKey("values");

        // New DataItem & basic.
        renderer.newDataItem(dataItem);
        renderer.addBasic();

        // Optionals.
        if (name || full) {
            renderer.addName();
        }
        if (label || full) {
            renderer.addLabel();
        }
        if (path || full) {
            renderer.addPath();
        }
        if (parent || full) {
            renderer.addParent();
        }
        if (audit || full) {
            renderer.addAudit();
        }
        if (wikiDoc || full) {
            renderer.addWikiDoc();
        }
        if (provenance || full) {
            renderer.addProvenance();
        }
        if ((itemDefinition || full) && (dataItem.getItemDefinition() != null)) {
            ItemDefinition id = dataItem.getItemDefinition();
            renderer.addItemDefinition(id);
        }
        if (values || full) {
            renderer.startValues();
            for (BaseItemValue itemValue : dataItemService.getItemValues(dataItem)) {
                renderer.newValue(itemValue);
            }
        }
    }

    public DataItemResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (renderer == null) {
            renderer = (DataItemResource.Renderer) resourceBeanFinder.getRenderer(DataItemResource.Renderer.class, requestWrapper);
        }
        return renderer;
    }

    @Override
    public DataItemValuesResource.DataItemValuesFilterValidator getValidator(RequestWrapper requestWrapper) {
        return (DataItemValuesResource.DataItemValuesFilterValidator)
                resourceBeanFinder.getValidator(
                        DataItemValuesResource.DataItemValuesFilterValidator.class, requestWrapper);
    }
}