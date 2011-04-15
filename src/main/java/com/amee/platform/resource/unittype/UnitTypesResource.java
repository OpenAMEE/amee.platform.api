package com.amee.platform.resource.unittype;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceAcceptor;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRenderer;
import com.amee.domain.unit.AMEEUnitType;

public interface UnitTypesResource {

    public static interface Builder extends ResourceBuilder {

        public UnitTypesResource.Renderer getRenderer(RequestWrapper requestWrapper);
    }

    public static interface Renderer extends ResourceRenderer {

        public void newUnitType(UnitTypeResource.Renderer renderer);

        public Object getObject();
    }

    public static interface FormAcceptor extends ResourceAcceptor {

        public Object handle(RequestWrapper requestWrapper, AMEEUnitType unitType);

        public UnitTypeResource.UnitTypeValidator getValidator(RequestWrapper requestWrapper);
    }
}