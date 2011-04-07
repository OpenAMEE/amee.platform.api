package com.amee.platform.resource.unit.v_3_5;

import com.amee.base.domain.Since;
import com.amee.domain.unit.AMEEUnit;
import com.amee.platform.resource.unit.UnitResource;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.5.0")
public class UnitDOMRenderer_3_5_0 implements UnitResource.Renderer {

    protected AMEEUnit unit;
    protected Element rootElem;
    protected Element unitElem;

    @Override
    public void start() {
        rootElem = new Element("Representation");
    }

    @Override
    public void ok() {
        rootElem.addContent(new Element("Status").setText("OK"));
    }

    public void newUnit(AMEEUnit unit) {
        this.unit = unit;
        unitElem = new Element("Unit");
        if (rootElem != null) {
            rootElem.addContent(unitElem);
        }
    }

    @Override
    public void addBasic() {
        unitElem.setAttribute("uid", unit.getUid());
        unitElem.addContent(new Element("Name").setText(unit.getName()));
    }

    public void addAudit() {
        unitElem.setAttribute("status", unit.getStatus().getName());
        unitElem.setAttribute("created", DATE_FORMAT.print(unit.getCreated().getTime()));
        unitElem.setAttribute("modified", DATE_FORMAT.print(unit.getModified().getTime()));
    }

    public String getMediaType() {
        return "application/xml";
    }

    public Object getObject() {
        return new Document(rootElem);
    }
}
