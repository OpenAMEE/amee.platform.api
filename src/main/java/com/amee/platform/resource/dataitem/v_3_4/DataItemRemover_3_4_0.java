package com.amee.platform.resource.dataitem.v_3_4;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.transaction.AMEETransaction;
import com.amee.domain.data.DataCategory;
import com.amee.domain.item.data.DataItem;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.dataitem.DataItemResource;
import com.amee.service.auth.ResourceAuthorizationService;
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
public class DataItemRemover_3_4_0 implements DataItemResource.Remover {

    @Autowired
    private InvalidationService invalidationService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private DataItemService dataItemService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Object handle(RequestWrapper requestWrapper) {

        // Get entities.
        DataCategory dataCategory = resourceService.getDataCategoryWhichHasItemDefinition(requestWrapper);
        DataItem dataItem = resourceService.getDataItem(requestWrapper, dataCategory);

        // Authorized?
        resourceAuthorizationService.ensureAuthorizedForRemove(
                requestWrapper.getAttributes().get("activeUserUid"), dataItem);

        // Handle DataItem removal.
        dataItemService.remove(dataItem);
        invalidationService.add(dataItem.getDataCategory());
        return ResponseHelper.getOK(requestWrapper);
    }
}