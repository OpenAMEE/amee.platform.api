package com.amee.platform.resource.dataitemvalue.v_3_4;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.transaction.AMEETransaction;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.BaseItemValue;
import com.amee.domain.item.data.BaseDataItemValue;
import com.amee.domain.item.data.DataItem;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.dataitemvalue.DataItemValueHistoryResource;
import com.amee.platform.resource.dataitemvalue.DataItemValueResource;
import com.amee.platform.resource.dataitemvalue.DataItemValuesResource;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.item.DataItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.4.0")
public class DataItemValueHistoryBuilder_3_4_0 implements DataItemValueHistoryResource.Builder {

    @Autowired
    private DataItemService dataItemService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    private DataItemValuesResource.Renderer renderer;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {
        // Get entities.
        DataCategory dataCategory = resourceService.getDataCategoryWhichHasItemDefinition(requestWrapper);
        DataItem dataItem = resourceService.getDataItem(requestWrapper, dataCategory);
        ItemValueDefinition itemValueDefinition = resourceService.getItemValueDefinition(requestWrapper, dataItem);
        // Authorized for DataItem?
        resourceAuthorizationService.ensureAuthorizedForBuild(
                requestWrapper.getAttributes().get("activeUserUid"), dataItem);
        // Handle the DataItem & ItemValueDefinition.
        handle(requestWrapper, dataItem, itemValueDefinition);
        DataItemValuesResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.ok();
        return renderer.getObject();
    }

    protected void handle(
            RequestWrapper requestWrapper,
            DataItem dataItem,
            ItemValueDefinition itemValueDefinition) {
        // Setup Renderer.
        DataItemValuesResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.start();
        // Add Data Item Values to Renderer and build.
        DataItemValueResource.Builder dataItemValueBuilder = getDataItemValueBuilder(requestWrapper);
        for (BaseItemValue itemValue : dataItemService.getAllItemValues(dataItem, itemValueDefinition.getPath())) {
            BaseDataItemValue dataItemValue = (BaseDataItemValue) itemValue;
            dataItemValueBuilder.handle(requestWrapper, dataItemValue);
            renderer.newDataItemValue(dataItemValueBuilder.getRenderer(requestWrapper));
        }
    }

    @Override
    public DataItemValuesResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (renderer == null) {
            renderer = (DataItemValuesResource.Renderer) resourceBeanFinder.getRenderer(DataItemValuesResource.Renderer.class, requestWrapper);
        }
        return renderer;
    }

    public DataItemValueResource.Builder getDataItemValueBuilder(RequestWrapper requestWrapper) {
        return (DataItemValueResource.Builder)
                resourceBeanFinder.getBuilder(DataItemValueResource.Builder.class, requestWrapper);
    }
}
