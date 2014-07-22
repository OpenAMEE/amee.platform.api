package com.amee.platform.resource.unit.v_3_5;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.transaction.AMEETransaction;
import com.amee.domain.unit.AMEEUnit;
import com.amee.domain.unit.AMEEUnitType;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.unit.UnitResource;
import com.amee.platform.resource.unit.UnitsResource;
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
public class UnitsBuilder_3_5_0 implements UnitsResource.Builder {

    @Autowired
    private UnitService unitService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    @Autowired
    private ResourceService resourceService;

    private UnitsResource.Renderer unitsRenderer;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {

        // Get entities.
        AMEEUnitType unitType = resourceService.getUnitType(requestWrapper, true);

        // Authorized?
        if (unitType != null) {
            resourceAuthorizationService.ensureAuthorizedForBuild(
                    requestWrapper.getAttributes().get("activeUserUid"), unitType);
        } else {
            resourceAuthorizationService.ensureAuthorizedForBuild(
                    requestWrapper.getAttributes().get("activeUserUid"));
        }

        // Disallow 'alternatives' matrix parameter.
        requestWrapper.getMatrixParameters().remove("alternatives");

        // Start Renderer.
        UnitsResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.start();

        // Add Unit.
        UnitResource.Builder unitBuilder = getUnitBuilder(requestWrapper);
        for (AMEEUnit unit : unitService.getUnits(unitType)) {
            unitBuilder.handle(requestWrapper, unit);
            renderer.newUnit(unitBuilder.getRenderer(requestWrapper));
        }

        // We're done!
        renderer.ok();
        return renderer.getObject();
    }

    @Override
    public UnitsResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (unitsRenderer == null) {
            unitsRenderer = (UnitsResource.Renderer) resourceBeanFinder.getRenderer(UnitsResource.Renderer.class, requestWrapper);
        }
        return unitsRenderer;
    }

    private UnitResource.Builder getUnitBuilder(RequestWrapper requestWrapper) {
        return (UnitResource.Builder)
                resourceBeanFinder.getBuilder(UnitResource.Builder.class, requestWrapper);
    }
}