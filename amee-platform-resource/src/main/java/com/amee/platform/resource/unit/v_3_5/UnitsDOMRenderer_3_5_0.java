package com.amee.platform.resource.unit.v_3_5;

import com.amee.base.domain.Since;
import com.amee.platform.resource.unit.UnitResource;
import com.amee.platform.resource.unit.UnitsResource;
import org.jdom2.Document;
import org.jdom2.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.5.0")
public class UnitsDOMRenderer_3_5_0 implements UnitsResource.Renderer {

    protected Element rootElem;
    protected Element unitsElem;

    @Override
    public void start() {
        rootElem = new Element("Representation");
        unitsElem = new Element("Units");
        rootElem.addContent(unitsElem);
    }

    @Override
    public void newUnit(UnitResource.Renderer renderer) {
        unitsElem.addContent(((Document) renderer.getObject()).getRootElement().getChild("Unit").detach());
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
