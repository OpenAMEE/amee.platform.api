package com.amee.platform.resource.unit;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceAcceptor;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRenderer;
import com.amee.domain.unit.AMEEUnit;

public interface UnitsResource {

    public static interface Builder extends ResourceBuilder {

        public UnitsResource.Renderer getRenderer(RequestWrapper requestWrapper);
    }

    public static interface Renderer extends ResourceRenderer {

        public void newUnit(UnitResource.Renderer renderer);

        public Object getObject();
    }

    public static interface FormAcceptor extends ResourceAcceptor {

        public Object handle(RequestWrapper requestWrapper, AMEEUnit unit);

        public UnitResource.UnitValidator getValidator(RequestWrapper requestWrapper);
    }
}