package com.amee.platform.resource.unittype.v_3_5;

import com.amee.base.domain.Since;
import com.amee.platform.resource.unittype.UnitTypeResource;
import com.amee.platform.resource.unittype.UnitTypesResource;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.5.0")
public class UnitTypesDOMRenderer_3_5_0 implements UnitTypesResource.Renderer {

    protected Element rootElem;
    protected Element unitTypesElem;

    @Override
    public void start() {
        rootElem = new Element("Representation");
        unitTypesElem = new Element("UnitTypes");
        rootElem.addContent(unitTypesElem);
    }

    @Override
    public void newUnitType(UnitTypeResource.Renderer renderer) {
        unitTypesElem.addContent(((Document) renderer.getObject()).getRootElement().getChild("UnitType").detach());
    }

    @Override
    public void ok() {
        rootElem.addContent(new Element("Status").setText("OK"));
    }

    @Override
    public String getMediaType() {
        return "application/xml";
    }

    @Override
    public Document getObject() {
        return new Document(rootElem);
    }
}
