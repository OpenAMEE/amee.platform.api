package com.amee.platform.resource.dataitem;

import org.jdom.Document;
import org.jdom.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class DataItemsDOMRenderer implements DataItemsRenderer {

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

    public void newDataItem(DataItemRenderer dataItemRenderer) {
        itemsElem.addContent(((Document) dataItemRenderer.getObject()).getRootElement().getChild("Item").detach());
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
