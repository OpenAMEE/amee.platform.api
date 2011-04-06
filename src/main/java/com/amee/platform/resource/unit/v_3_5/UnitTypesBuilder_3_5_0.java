package com.amee.platform.resource.unit.v_3_5;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.transaction.AMEETransaction;
import com.amee.domain.unit.AMEEUnitType;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.unit.UnitTypeResource;
import com.amee.platform.resource.unit.UnitTypesResource;
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
public class UnitTypesBuilder_3_5_0 implements UnitTypesResource.Builder {

    @Autowired
    private UnitService unitService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    @Autowired
    private ResourceService resourceService;

    private UnitTypesResource.Renderer unitTypesRenderer;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {

        // Authorized?
        resourceAuthorizationService.ensureAuthorizedForBuild(
                requestWrapper.getAttributes().get("activeUserUid"));

        // Start Renderer.
        UnitTypesResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.start();

        // Add UnitType.
        UnitTypeResource.Builder unitTypeBuilder = getUnitTypeBuilder(requestWrapper);
        for (AMEEUnitType unitType : unitService.getUnitTypes()) {
            unitTypeBuilder.handle(requestWrapper, unitType);
            renderer.newUnitType(unitTypeBuilder.getRenderer(requestWrapper));
        }

        // We're done!
        renderer.ok();
        return renderer.getObject();
    }

    public UnitTypesResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (unitTypesRenderer == null) {
            unitTypesRenderer = (UnitTypesResource.Renderer) resourceBeanFinder.getRenderer(UnitTypesResource.Renderer.class, requestWrapper);
        }
        return unitTypesRenderer;
    }

    private UnitTypeResource.Builder getUnitTypeBuilder(RequestWrapper requestWrapper) {
        return (UnitTypeResource.Builder)
                resourceBeanFinder.getBuilder(UnitTypeResource.Builder.class, requestWrapper);
    }
}