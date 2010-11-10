package com.amee.platform.resource.dataitem.v_3_0;

import com.amee.base.domain.Since;
import com.amee.platform.resource.dataitem.DataItemResource;
import com.amee.platform.resource.dataitem.DataItemsResource;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.0.0")
public class DataItemsDOMRenderer_3_0_0 implements DataItemsResource.Renderer {

    private Element rootElem;
    private Element itemsElem;

    public void start() {
        rootElem = new Element("Representation");
        itemsElem = new Element("Items");
        rootElem.addContent(itemsElem);
    }

    public void ok() {
        rootElem.addContent(new Element("Status").setText("OK"));
    }

    public void newDataItem(DataItemResource.Renderer renderer) {
        itemsElem.addContent(((Document) renderer.getObject()).getRootElement().getChild("Item").detach());
    }

    public void setTruncated(boolean truncated) {
        itemsElem.setAttribute("truncated", "" + truncated);
    }

    public String getMediaType() {
        return "application/xml";
    }

    public Object getObject() {
        return new Document(rootElem);
    }
}
