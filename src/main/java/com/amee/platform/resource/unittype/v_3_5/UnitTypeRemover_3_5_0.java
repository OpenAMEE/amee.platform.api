package com.amee.platform.resource.unittype.v_3_5;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.transaction.AMEETransaction;
import com.amee.domain.unit.AMEEUnitType;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.unittype.UnitTypeResource;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.unit.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.5.0")
public class UnitTypeRemover_3_5_0 implements UnitTypeResource.Remover {

    @Autowired
    private UnitService unitService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Object handle(RequestWrapper requestWrapper) {

        // Get entities.
        AMEEUnitType unitType = resourceService.getUnitType(requestWrapper);

        // Authorized?
        resourceAuthorizationService.ensureAuthorizedForRemove(
                requestWrapper.getAttributes().get("activeUserUid"), unitType);

        // Handle UnitType removal.
        unitService.remove(unitType);
        return ResponseHelper.getOK(requestWrapper);
    }
}