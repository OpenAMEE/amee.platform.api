package com.amee.platform.resource.unit.v_3_5;

import com.amee.base.domain.Since;
import com.amee.domain.unit.AMEEUnitType;
import com.amee.platform.resource.unit.UnitTypeResource;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.5.0")
public class UnitTypeDOMRenderer_3_5_0 implements UnitTypeResource.Renderer {

    protected AMEEUnitType unitType;
    protected Element rootElem;
    protected Element unitTypeElem;

    @Override
    public void start() {
        rootElem = new Element("Representation");
    }

    @Override
    public void ok() {
        rootElem.addContent(new Element("Status").setText("OK"));
    }

    public void newUnitType(AMEEUnitType unitType) {
        this.unitType = unitType;
        unitTypeElem = new Element("UnitType");
        if (rootElem != null) {
            rootElem.addContent(unitTypeElem);
        }
    }

    @Override
    public void addBasic() {
        unitTypeElem.setAttribute("uid", unitType.getUid());
        unitTypeElem.addContent(new Element("Name").setText(unitType.getName()));
    }

    public void addAudit() {
        unitTypeElem.setAttribute("status", unitType.getStatus().getName());
        unitTypeElem.setAttribute("created", DATE_FORMAT.print(unitType.getCreated().getTime()));
        unitTypeElem.setAttribute("modified", DATE_FORMAT.print(unitType.getModified().getTime()));
    }

    public String getMediaType() {
        return "application/xml";
    }

    public Object getObject() {
        return new Document(rootElem);
    }
}
