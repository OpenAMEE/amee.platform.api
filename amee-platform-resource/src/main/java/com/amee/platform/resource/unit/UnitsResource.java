package com.amee.platform.resource.unit;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceAcceptor;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRenderer;
import com.amee.domain.unit.AMEEUnit;

public interface UnitsResource {

    interface Builder extends ResourceBuilder {

        UnitsResource.Renderer getRenderer(RequestWrapper requestWrapper);
    }

    interface Renderer extends ResourceRenderer {

        void newUnit(UnitResource.Renderer renderer);
    }

    interface FormAcceptor extends ResourceAcceptor {

        Object handle(RequestWrapper requestWrapper, AMEEUnit unit);

        UnitResource.UnitValidator getValidator(RequestWrapper requestWrapper);
    }
}