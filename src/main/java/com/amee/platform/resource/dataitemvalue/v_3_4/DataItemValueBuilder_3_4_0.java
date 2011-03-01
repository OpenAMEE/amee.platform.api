package com.amee.platform.resource.dataitemvalue.v_3_4;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.transaction.AMEETransaction;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.data.BaseDataItemValue;
import com.amee.domain.item.data.DataItem;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.dataitemvalue.DataItemValueResource;
import com.amee.service.auth.ResourceAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.4.0")
public class DataItemValueBuilder_3_4_0 implements DataItemValueResource.Builder {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    private DataItemValueResource.Renderer renderer;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {
        // Get entities.
        DataCategory dataCategory = resourceService.getDataCategoryWhichHasItemDefinition(requestWrapper);
        DataItem dataItem = resourceService.getDataItem(requestWrapper, dataCategory);
        ItemValueDefinition itemValueDefinition = resourceService.getItemValueDefinition(requestWrapper, dataItem);
        BaseDataItemValue dataItemValue = resourceService.getDataItemValue(requestWrapper, dataItem, itemValueDefinition);
        // Authorized for DataItem?
        resourceAuthorizationService.ensureAuthorizedForBuild(
                requestWrapper.getAttributes().get("activeUserUid"), dataItem);
        // Handle the DataItem.
        handle(requestWrapper, dataItemValue);
        DataItemValueResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.ok();
        return renderer.getObject();
    }

    @Override
    public void handle(RequestWrapper requestWrapper, BaseDataItemValue dataItemValue) {

        // Get the Renderer.
        DataItemValueResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.start();

        // Collect rendering options from matrix params.
        boolean full = requestWrapper.getMatrixParameters().containsKey("full");
        boolean path = requestWrapper.getMatrixParameters().containsKey("path");
        boolean category = requestWrapper.getMatrixParameters().containsKey("category");
        boolean item = requestWrapper.getMatrixParameters().containsKey("item");
        boolean audit = requestWrapper.getMatrixParameters().containsKey("audit");
        boolean itemValueDefinition = requestWrapper.getMatrixParameters().containsKey("itemValueDefinition");

        // New DataItemValue & basic.
        renderer.newDataItemValue(dataItemValue);
        renderer.addBasic();

        // Optionals.
        if (path || full) {
            renderer.addPath();
        }
        if (category || full) {
            renderer.addDataCategory();
        }
        if (item || full) {
            renderer.addDataItem();
        }
        if (audit || full) {
            renderer.addAudit();
        }
        if ((itemValueDefinition || full) && (dataItemValue.getItemValueDefinition() != null)) {
            renderer.addItemValueDefinition(dataItemValue.getItemValueDefinition());
        }
    }

    @Override
    public DataItemValueResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (renderer == null) {
            renderer = (DataItemValueResource.Renderer) resourceBeanFinder.getRenderer(DataItemValueResource.Renderer.class, requestWrapper);
        }
        return renderer;
    }
}
