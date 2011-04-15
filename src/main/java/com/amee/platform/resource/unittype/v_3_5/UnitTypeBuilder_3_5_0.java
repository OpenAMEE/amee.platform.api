package com.amee.platform.resource.unittype.v_3_5;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.transaction.AMEETransaction;
import com.amee.domain.unit.AMEEUnitType;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.unittype.UnitTypeResource;
import com.amee.service.auth.ResourceAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.5.0")
public class UnitTypeBuilder_3_5_0 implements UnitTypeResource.Builder {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    private UnitTypeResource.Renderer renderer;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {

        // Get resource entities for this request.
        AMEEUnitType unitType = resourceService.getUnitType(requestWrapper);

        // Authorized for UnitType?
        resourceAuthorizationService.ensureAuthorizedForBuild(
                requestWrapper.getAttributes().get("activeUserUid"), unitType);

        // Handle the UnitType.
        handle(requestWrapper, unitType);
        UnitTypeResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.ok();
        return renderer.getObject();
    }

    @Override
    public void handle(RequestWrapper requestWrapper, AMEEUnitType unitType) {

        // Get the Renderer.
        UnitTypeResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.start();

        // Collect rendering options from matrix params.
        boolean full = requestWrapper.getMatrixParameters().containsKey("full");
        boolean audit = requestWrapper.getMatrixParameters().containsKey("audit");

        // New UnitType & basic.
        renderer.newUnitType(unitType);
        renderer.addBasic();

        // Optionals.
        if (audit || full) {
            renderer.addAudit();
        }
    }

    @Override
    public UnitTypeResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (renderer == null) {
            renderer = (UnitTypeResource.Renderer) resourceBeanFinder.getRenderer(UnitTypeResource.Renderer.class, requestWrapper);
        }
        return renderer;
    }
}
