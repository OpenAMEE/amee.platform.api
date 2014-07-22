package com.amee.platform.resource.unittype;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceAcceptor;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRenderer;
import com.amee.domain.unit.AMEEUnitType;

public interface UnitTypesResource {

    interface Builder extends ResourceBuilder {

        UnitTypesResource.Renderer getRenderer(RequestWrapper requestWrapper);
    }

    interface Renderer extends ResourceRenderer {

        void newUnitType(UnitTypeResource.Renderer renderer);
    }

    interface FormAcceptor extends ResourceAcceptor {

        Object handle(RequestWrapper requestWrapper, AMEEUnitType unitType);

        UnitTypeResource.UnitTypeValidator getValidator(RequestWrapper requestWrapper);
    }
}