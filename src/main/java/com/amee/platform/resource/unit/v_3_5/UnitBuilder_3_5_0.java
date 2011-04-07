package com.amee.platform.resource.unit.v_3_5;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.transaction.AMEETransaction;
import com.amee.domain.unit.AMEEUnit;
import com.amee.domain.unit.AMEEUnitType;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.unit.UnitResource;
import com.amee.service.auth.ResourceAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.5.0")
public class UnitBuilder_3_5_0 implements UnitResource.Builder {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    private UnitResource.Renderer renderer;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {

        // Get resource entities for this request.
        AMEEUnitType unitType = resourceService.getUnitType(requestWrapper);
        AMEEUnit unit = resourceService.getUnit(requestWrapper, unitType);

        // Authorized for Unit?
        resourceAuthorizationService.ensureAuthorizedForBuild(
                requestWrapper.getAttributes().get("activeUserUid"), unit);

        // Handle the Unit.
        handle(requestWrapper, unit);
        UnitResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.ok();
        return renderer.getObject();
    }

    @Override
    public void handle(RequestWrapper requestWrapper, AMEEUnit unit) {

        // Get the Renderer.
        UnitResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.start();

        // Collect rendering options from matrix params.
        boolean full = requestWrapper.getMatrixParameters().containsKey("full");
        boolean audit = requestWrapper.getMatrixParameters().containsKey("audit");
        boolean symbols = requestWrapper.getMatrixParameters().containsKey("symbols");
        boolean unitType = requestWrapper.getMatrixParameters().containsKey("unitType");
        boolean internalUnit = requestWrapper.getMatrixParameters().containsKey("internalUnit");

        // New Unit & basic.
        renderer.newUnit(unit);
        renderer.addBasic();

        // Optionals.
        if (audit || full) {
            renderer.addAudit();
        }
        if (symbols || full) {
            renderer.addSymbols();
        }
        if (unitType || full) {
            renderer.addUnitType();
        }
        if (internalUnit || full) {
            renderer.addInternalUnit();
        }
    }

    @Override
    public UnitResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (renderer == null) {
            renderer = (UnitResource.Renderer) resourceBeanFinder.getRenderer(UnitResource.Renderer.class, requestWrapper);
        }
        return renderer;
    }
}
