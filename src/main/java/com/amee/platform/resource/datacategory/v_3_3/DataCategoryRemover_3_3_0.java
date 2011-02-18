package com.amee.platform.resource.datacategory.v_3_3;

import com.amee.base.domain.Since;
import com.amee.base.resource.MissingAttributeException;
import com.amee.base.resource.NotFoundException;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.transaction.AMEETransaction;
import com.amee.domain.data.DataCategory;
import com.amee.platform.resource.datacategory.DataCategoryResource;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.data.DataService;
import com.amee.service.invalidation.InvalidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.3.0")
public class DataCategoryRemover_3_3_0 implements DataCategoryResource.Remover {

    @Autowired
    private InvalidationService invalidationService;

    @Autowired
    private DataService dataService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Object handle(RequestWrapper requestWrapper) {
        String dataCategoryIdentifier = requestWrapper.getAttributes().get("categoryIdentifier");
        if (dataCategoryIdentifier != null) {
            DataCategory dataCategory = dataService.getDataCategoryByIdentifier(dataCategoryIdentifier);
            if (dataCategory != null) {
                // Authorized?
                resourceAuthorizationService.ensureAuthorizedForRemove(
                        requestWrapper.getAttributes().get("activeUserUid"), dataCategory);
                // Handle DataCategory removal.
                dataService.remove(dataCategory);
                invalidationService.add(dataCategory);
                return ResponseHelper.getOK(requestWrapper);
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new MissingAttributeException("dataCategoryIdentifier");
        }
    }
}