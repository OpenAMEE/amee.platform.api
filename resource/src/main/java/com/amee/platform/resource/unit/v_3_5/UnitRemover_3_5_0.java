package com.amee.platform.resource.unit.v_3_5;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.transaction.AMEETransaction;
import com.amee.domain.unit.AMEEUnit;
import com.amee.domain.unit.AMEEUnitType;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.unit.UnitResource;
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
public class UnitRemover_3_5_0 implements UnitResource.Remover {

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
        AMEEUnit unit = resourceService.getUnit(requestWrapper, unitType);

        // Authorized?
        resourceAuthorizationService.ensureAuthorizedForRemove(
                requestWrapper.getAttributes().get("activeUserUid"), unit);

        // Handle Unit removal.
        unitService.remove(unit);
        return ResponseHelper.getOK(requestWrapper, null, unit.getUid());
    }
}